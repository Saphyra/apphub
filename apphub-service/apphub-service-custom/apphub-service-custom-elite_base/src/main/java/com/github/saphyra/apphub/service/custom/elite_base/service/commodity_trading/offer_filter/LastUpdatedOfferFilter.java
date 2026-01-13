package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.type.ItemTypeDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_BATCH_SIZE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_THREAD_COUNT;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LastUpdatedOfferFilter {
    private final DateTimeUtil dateTimeUtil;
    private final LastUpdateDao lastUpdateDao;
    private final ItemTypeDao itemTypeDao;
    private final ExecutorServiceBean executorServiceBean;

    public List<OfferDetail> filter(List<OfferDetail> offerDetails, CommodityTradingRequest request) {
        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(request.getMaxTimeSinceLastUpdated());

        ItemType itemType = itemTypeDao.findByIdValidated(request.getItemName()).getType();

        List<OfferDetail> result = executorServiceBean.processBatch(offerDetails, partition -> processBatch(partition, itemType, expiration), COMMODITY_TRADING_BATCH_SIZE, COMMODITY_TRADING_THREAD_COUNT);

        log.debug("Filtered {} offers based on lastUpdated", offerDetails.size() - result.size());
        return result;
    }

    private List<OfferDetail> processBatch(List<OfferDetail> offerDetails, ItemType itemType, LocalDateTime expiration) {
        List<UUID> externalReferences = offerDetails.stream().map(offerDetail -> offerDetail.getCommodityLocationData().getExternalReference()).toList();
        Map<UUID, LocalDateTime> lastUpdates = Utils.measuredOperation(
            () -> loadLastUpdates(itemType, externalReferences),
            "Queried %s of LastUpdates in {} ms".formatted(offerDetails.size())
        );

        return offerDetails.stream()
            .filter(offerDetail -> {
                LocalDateTime lastUpdated = lastUpdates.get(offerDetail.getCommodityLocationData().getExternalReference());
                return lastUpdated.isAfter(expiration);
            })
            .toList();
    }

    private Map<UUID, LocalDateTime> loadLastUpdates(ItemType itemType, List<UUID> externalReferences) {
        return lastUpdateDao.getLastUpdates(itemType, externalReferences)
            .stream()
            .collect(Collectors.toMap(LastUpdate::getExternalReference, LastUpdate::getLastUpdate));
    }
}
