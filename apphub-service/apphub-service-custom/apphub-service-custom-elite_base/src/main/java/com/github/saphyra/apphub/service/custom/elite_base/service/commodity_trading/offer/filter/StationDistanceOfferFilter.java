package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationDistanceOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        Optional<Double> stationDistanceFromStar = offerDetail.getStationDistanceFromStar();
        if (stationDistanceFromStar.isEmpty()) {
            Boolean result = request.getIncludeUnknownStationDistance();
            if (!result && log.isDebugEnabled()) {
                log.debug("Filtered offer from station with unknown distance: {}", offerDetail);
            }
            return result;
        }

        boolean result = stationDistanceFromStar.get() <= request.getMaxStationDistance();
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtered offer from too far station: {}", offerDetail);
        }
        return result;
    }
}
