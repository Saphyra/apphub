package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceStationOfferFilter implements OfferFilter {
    @Override
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
        if (request.getIncludeSurfaceStations()) {
            return true;
        }

        StationType stationType = StationType.valueOf(response.getLocationType());

        boolean result = stationType != StationType.SURFACE_STATION && stationType != StationType.ON_FOOT_SETTLEMENT;
        if (!result) {
            log.info("Filtered offer at surface station: {}", response);
        }
        return result;
    }
}
