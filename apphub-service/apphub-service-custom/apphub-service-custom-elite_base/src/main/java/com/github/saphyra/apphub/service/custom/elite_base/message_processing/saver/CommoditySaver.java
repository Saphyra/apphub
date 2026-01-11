package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.TradingDaoSupport;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.type.ItemTypeDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.commodity.EdCommodity;
import com.google.common.util.concurrent.Striped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CommoditySaver {
    private static final Striped<Lock> LOCKS = Striped.lock(8);

    private final TradingDaoSupport tradingDaoSupport;
    private final CommodityDataTransformer commodityDataTransformer;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;
    private final PerformanceReporter performanceReporter;
    private final CommodityAveragePriceSaver commodityAveragePriceSaver;
    private final ItemTypeDao itemTypeDao;

    public void saveAll(LocalDateTime timestamp, ItemType type, ItemLocationType locationType, UUID externalReference, Long marketId, EdCommodity[] commodities) {
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

        saveAll(timestamp, type, locationType, externalReference, marketId, commodityDataList);
    }

    @SneakyThrows
    public void saveAll(LocalDateTime timestamp, ItemType type, ItemLocationType locationType, UUID externalReference, Long marketId, List<CommodityData> commodities) {
        if (isNull(marketId)) {
            throw new IllegalArgumentException("Both locationType or externalReference and marketId is null");
        }

        log.debug("Saving commodities for location {} and type {}", locationType, type);

        LockKey key = new LockKey(externalReference, type);
        Lock lock = LOCKS.get(key);
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }

        try {
            commodities = filterEmptyCommodities(commodities);

            LastUpdate lastUpdate = saveLastUpdate(timestamp, type, externalReference);

            Map<String, Tradeable> existingCommodities = getExistingCommodities(externalReference, type, marketId)
                .stream()
                .collect(Collectors.toMap(Tradeable::getItemName, Function.identity()));

            List<Tradeable> modifiedCommodities = commodities.stream()
                .map(commodity -> commodityDataTransformer.transform(existingCommodities.get(commodity.getName()), timestamp, type, locationType, externalReference, marketId, commodity, lastUpdate))
                .flatMap(Optional::stream)
                .toList();

            List<String> newCommodityNames = commodities.stream()
                .map(CommodityData::getName)
                .toList();
            List<Tradeable> deletedCommodities = existingCommodities.values()
                .stream()
                .filter(c -> !newCommodityNames.contains(c.getItemName()))
                .toList();

            Map<String, Integer> commodityPrices = commodities.stream()
                .filter(commodityData -> nonNull(commodityData.getAveragePrice()))
                .filter(commodityData -> commodityData.getAveragePrice() > 0)
                .collect(Collectors.toMap(CommodityData::getName, CommodityData::getAveragePrice));
            commodityAveragePriceSaver.saveAveragePrices(lastUpdate.getLastUpdate(), commodityPrices);

            List<String> commodityNames = commodities.stream()
                .map(CommodityData::getName)
                .toList();
            itemTypeDao.saveAll(type, commodityNames);

            performanceReporter.wrap(
                () -> tradingDaoSupport.deleteAll(type, deletedCommodities),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_COMMODITIES_DELETE_ALL.name()
            );
            log.debug("Deleted {} commodities", deletedCommodities.size());

            performanceReporter.wrap(
                () -> tradingDaoSupport.saveAll(type, modifiedCommodities),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_COMMODITIES_SAVE_ALL.name()
            );
            log.debug("Saved {} commodities", modifiedCommodities.size());

            log.debug("Saved commodities for location {} and type {}", locationType, type);
        } finally {
            lock.unlock();
        }
    }

    private LastUpdate saveLastUpdate(LocalDateTime timestamp, ItemType type, UUID externalReference) {
        LastUpdate lastUpdate = lastUpdateFactory.create(externalReference, type, timestamp);
        lastUpdateDao.save(lastUpdate);
        log.debug("LastUpdate saved for location {} and type {}", externalReference, type);

        return lastUpdate;
    }

    private static List<CommodityData> filterEmptyCommodities(List<CommodityData> commodities) {
        return commodities.stream()
            .filter(commodityData -> commodityData.getStock() > 0 || commodityData.getDemand() > 0)
            .toList();
    }

    private List<Tradeable> getExistingCommodities(UUID externalReference, ItemType type, Long marketId) {
        List<Tradeable> commodities = performanceReporter.wrap(
            () -> tradingDaoSupport.getByMarketId(type, marketId),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.SAVE_COMMODITIES_QUERY.name()
        );

        List<Tradeable> incorrectCommodities = commodities.stream()
            .filter(commodity -> !commodity.getExternalReference().equals(externalReference))
            .toList();
        log.debug("Found {} incorrect commodities.", incorrectCommodities.size());
        if (!incorrectCommodities.isEmpty()) {
            tradingDaoSupport.deleteAll(type, incorrectCommodities);
            log.debug("{} incorrect commodities were deleted.", incorrectCommodities.size());
        }

        return commodities.stream()
            .filter(commodity -> !incorrectCommodities.contains(commodity))
            .toList();
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

    @Data
    private static class LockKey {
        private final UUID externalReference;
        private final ItemType itemType;
    }
}
