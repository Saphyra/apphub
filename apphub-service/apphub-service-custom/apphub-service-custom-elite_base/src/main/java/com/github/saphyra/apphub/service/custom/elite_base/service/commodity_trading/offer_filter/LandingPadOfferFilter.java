package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.LandingPad;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class LandingPadOfferFilter implements OfferFilter {
    @Override
    public boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        LandingPad landingPad = Optional.ofNullable(offerDetail.getCommodityLocationData().getStationType())
            .map(StationType::getLandingPad)
            .orElse(null);
        if (isNull(landingPad)) {
            boolean result = request.getIncludeUnknownLandingPad();
            if (!result) {
                log.debug("Filtered offer from station with unknown landing pad: {}", offerDetail);
            }
            return result;
        }

        boolean result = landingPad.isLargeEnough(request.getMinLandingPad());
        if (!result) {
            log.debug("Filtered offer from station with too small landing pad: {}", offerDetail);
        }
        return result;
    }
}
