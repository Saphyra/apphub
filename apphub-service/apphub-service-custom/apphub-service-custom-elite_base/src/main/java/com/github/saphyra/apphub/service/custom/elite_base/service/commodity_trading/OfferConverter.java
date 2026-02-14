package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingResponseItem;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class OfferConverter {
    List<CommodityTradingResponseItem> convert(List<OfferDetail> offers) {
        return offers.stream()
            .map(this::convert)
            .toList();
    }

    private CommodityTradingResponseItem convert(OfferDetail offerDetail) {
        return CommodityTradingResponseItem.builder()
            .starId(offerDetail.getStarSystemId())
            .starName(offerDetail.getStarName())
            .starSystemDistance(offerDetail.getDistanceFromReferenceSystem())
            .externalReference(offerDetail.getExternalReference())
            .locationName(offerDetail.getLocationName())
            .locationType(offerDetail.getLocationType().name())
            .stationDistance(offerDetail.getStationDistanceFromStar().orElse(null))
            .landingPad(offerDetail.getStationType().getLandingPad())
            .tradeAmount(offerDetail.getAmount())
            .price(offerDetail.getPrice())
            .controllingPower(offerDetail.getControllingPower().map(Enum::name).orElse(null))
            .powers(offerDetail.getPowers().map(powers -> powers.stream().map(Enum::name).toList()).orElse(null))
            .powerplayState(offerDetail.getPowerplayState().map(Enum::name).orElse(null))
            .lastUpdated(offerDetail.getLastUpdated())
            .build();
    }
}
