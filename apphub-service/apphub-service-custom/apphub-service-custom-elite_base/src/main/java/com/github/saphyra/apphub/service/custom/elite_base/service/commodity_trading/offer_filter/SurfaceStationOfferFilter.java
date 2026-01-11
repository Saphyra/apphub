package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.CommodityLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceStationOfferFilter implements OfferFilter {
    @Override
    public boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        if (request.getIncludeSurfaceStations()) {
            return true;
        }

        StationType stationType = Optional.ofNullable(offerDetail.getCommodityLocationData())
            .map(CommodityLocationData::getStationType)
            .orElse(null);
        if (isNull(stationType)) {
            return false;
        }

        boolean result = stationType != StationType.SURFACE_STATION && stationType != StationType.ON_FOOT_SETTLEMENT;
        if (!result) {
            log.debug("Filtered offer at surface station: {}", offerDetail);
        }
        return result;
    }
}
