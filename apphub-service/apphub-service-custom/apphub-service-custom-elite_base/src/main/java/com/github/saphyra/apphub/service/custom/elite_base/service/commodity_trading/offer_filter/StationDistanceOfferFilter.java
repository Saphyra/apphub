package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StationDistanceOfferFilter implements OfferFilter {
    @Override
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
        if (isNull(response.getStationDistance())) {
            Boolean result = request.getIncludeUnknownStationDistance();
            if (!result) {
                log.info("Filtered offer from station with unknown distance: {}", response);
            }
            return result;
        }

        boolean result = response.getStationDistance() <= request.getMaxStationDistance();
        if (!result) {
            log.info("Filtered offer from too far station: {}", response);
        }
        return result;
    }
}
