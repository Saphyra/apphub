package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_BATCH_SIZE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_THREAD_COUNT;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OfferDetailConverter {
    private final LastUpdateDao lastUpdateDao;
    private final ExecutorServiceBean executorServiceBean;

    public List<CommodityTradingResponse> convert(List<OfferDetail> filteredOffers) {
        return executorServiceBean.processBatch(filteredOffers, this::convertBatch, COMMODITY_TRADING_BATCH_SIZE, COMMODITY_TRADING_THREAD_COUNT);
    }

    private List<CommodityTradingResponse> convertBatch(List<OfferDetail> filteredOffers) {
        Map<UUID, ItemType> lastUpdateIdMap = filteredOffers.stream()
            .collect(Collectors.toMap(offerDetail -> offerDetail.getCommodityLocationData().getExternalReference(), o -> o.getTradingItem().getItemType()));

        Map<UUID, LocalDateTime> lastUpdates = lastUpdateDao.findAllById(lastUpdateIdMap)
            .stream()
            .collect(Collectors.toMap(LastUpdate::getExternalReference, LastUpdate::getLastUpdate));

        return filteredOffers.stream()
            .map(offerDetail -> convert(offerDetail, lastUpdates))
            .toList();
    }

    private CommodityTradingResponse convert(OfferDetail offerDetail, Map<UUID, LocalDateTime> lastUpdates) {
        return CommodityTradingResponse.builder()
            .starId(offerDetail.getStarSystem().getId())
            .starName(offerDetail.getStarSystem().getStarName())
            .starSystemDistance(offerDetail.getDistanceFromReferenceSystem())
            .externalReference(offerDetail.getCommodityLocationData().getExternalReference())
            .locationName(offerDetail.getCommodityLocationData().getLocationName())
            .locationType(Optional.ofNullable(offerDetail.getCommodityLocationData().getStationType()).map(Enum::name).orElse(null))
            .stationDistance(offerDetail.getStationDistanceFromStar())
            .landingPad(Optional.ofNullable(offerDetail.getCommodityLocationData().getStationType()).map(StationType::getLandingPad).orElse(null))
            .tradeAmount(offerDetail.getTradingItem().getTradeAmount(offerDetail.getTradeMode()))
            .price(offerDetail.getTradingItem().getPrice(offerDetail.getTradeMode()))
            .controllingPower(Optional.ofNullable(offerDetail.getStarSystemData()).map(StarSystemData::getControllingPower).map(Enum::name).orElse(null))
            .powers(Optional.ofNullable(offerDetail.getStarSystemData()).map(StarSystemData::getPowers).map(powers -> powers.stream().map(Enum::name).toList()).orElse(null)) //TODO batch power query
            .powerplayState(Optional.ofNullable(offerDetail.getStarSystemData()).map(StarSystemData::getPowerplayState).map(Enum::name).orElse(null))
            .lastUpdated(lastUpdates.get(offerDetail.getCommodityLocationData().getExternalReference()))
            .build();
    }
}
