package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.CommodityLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceStationOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        if (request.getIncludeSurfaceStations()) {
            return true;
        }

        StationType stationType = Optional.ofNullable(offerDetail.getCommodityLocationData())
            .map(CommodityLocationData::getStationType)
            .orElse(null);
        if (isNull(stationType)) {
            if (log.isDebugEnabled()) {
                log.debug("Filtered offer with unknown station type: {}", offerDetail);
            }
            return false;
        }

        boolean result = stationType != StationType.SURFACE_STATION && stationType != StationType.ON_FOOT_SETTLEMENT;
        if (!result) {
            log.debug("Filtered offer at surface station: {}", offerDetail);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return OfferFilterOrder.DEFAULT_ORDER.getOrder();
    }
}
