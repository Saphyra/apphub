package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.commodity.EdCommodity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommoditySaver {
    private final CommodityDao commodityDao;
    private final CommodityDataTransformer commodityDataTransformer;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;
    private final PerformanceReporter performanceReporter;

    public void saveAll(LocalDateTime timestamp, CommodityType type, CommodityLocation commodityLocation, UUID externalReference, Long marketId, EdCommodity[] commodities) {
        List<CommodityData> commodityDataList = Arrays.stream(commodities)
            .map(edCommodity -> CommodityData.builder()
                .name(edCommodity.getName())
                .buyPrice(edCommodity.getSellPrice()) //Have to reverse data because broker follows opposite logic
                .sellPrice(edCommodity.getBuyPrice()) //Have to reverse data because broker follows opposite logic
                .stock(edCommodity.getStock())
                .demand(edCommodity.getDemand())
                .averagePrice(edCommodity.getAveragePrice())
                .build())
            .toList();

        saveAll(timestamp, type, commodityLocation, externalReference, marketId, commodityDataList);
    }

    public synchronized void saveAll(LocalDateTime timestamp, CommodityType type, CommodityLocation commodityLocation, UUID externalReference, Long marketId, List<CommodityData> commodities) {
        if ((isNull(commodityLocation) || isNull(externalReference)) && isNull(marketId)) {
            throw new IllegalArgumentException("Both commodityLocation or externalReference and marketId is null");
        }

        lastUpdateDao.save(lastUpdateFactory.create(externalReference, type, timestamp));

        Map<String, Commodity> existingCommodities = performanceReporter.wrap(
                () -> commodityDao.getByExternalReferenceOrMarketId(externalReference, marketId),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_COMMODITIES_QUERY.name()
            )
            .stream()
            .collect(Collectors.toMap(Commodity::getCommodityName, Function.identity()));

        List<Commodity> modifiedCommodities = commodities.stream()
            .map(edCommodity -> commodityDataTransformer.transform(existingCommodities.get(edCommodity.getName()), timestamp, type, commodityLocation, externalReference, marketId, edCommodity))
            .flatMap(Optional::stream)
            .toList();

        List<String> newCommodityNames = commodities.stream()
            .map(CommodityData::getName)
            .toList();
        List<Commodity> deletedCommodities = existingCommodities.values()
            .stream()
            .filter(c -> !newCommodityNames.contains(c.getCommodityName()))
            .toList();

        performanceReporter.wrap(
            () -> commodityDao.deleteAll(deletedCommodities),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.SAVE_COMMODITIES_DELETE_ALL.name()
        );
        performanceReporter.wrap(
            () -> commodityDao.saveAll(modifiedCommodities),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.SAVE_COMMODITIES_SAVE_ALL.name()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @Data
    public static class CommodityData {
        private final String name;

        @Builder.Default
        private final Integer buyPrice = 0; //The price the station buys

        @Builder.Default
        private final Integer sellPrice = 0; //The price the station sells

        @Builder.Default
        private final Integer stock = 0;

        @Builder.Default
        private final Integer demand = 0;

        @Builder.Default
        private final Integer averagePrice = 0;

        public static class CommodityDataBuilder {
            public CommodityDataBuilder name(String name) {
                this.name = name.replace("$", "").replace("_name;", "");
                return this;
            }
        }
    }
}
