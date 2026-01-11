package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationDistanceOfferFilter implements OfferFilter {
    @Override
    public boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        if (isNull(offerDetail.getStationDistanceFromStar())) {
            Boolean result = request.getIncludeUnknownStationDistance();
            if (!result) {
                log.debug("Filtered offer from station with unknown distance: {}", offerDetail);
            }
            return result;
        }

        boolean result = offerDetail.getStationDistanceFromStar() <= request.getMaxStationDistance();
        if (!result) {
            log.debug("Filtered offer from too far station: {}", offerDetail);
        }
        return result;
    }
}
