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
public class LandingPadOfferFilter implements OfferFilter {
    @Override
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
        if (isNull(response.getLandingPad())) {
            boolean result = request.getIncludeUnknownLandingPad();
            if (!result) {
                log.info("Filtered offer from station with unknown landing pad: {}", response);
            }
            return result;
        }

        boolean result = response.getLandingPad()
            .isLargeEnough(request.getMinLandingPad());
        if (!result) {
            log.info("Filtered offer from station with too small landing pad: {}", response);
        }
        return result;
    }
}
