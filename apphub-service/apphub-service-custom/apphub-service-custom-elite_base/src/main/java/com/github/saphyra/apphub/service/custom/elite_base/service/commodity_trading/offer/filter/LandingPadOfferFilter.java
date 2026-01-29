package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.LandingPad;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class LandingPadOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        LandingPad landingPad = offerDetail.getStationType().getLandingPad();
        if (isNull(landingPad)) {
            boolean result = request.getIncludeUnknownLandingPad();
            if (!result && log.isDebugEnabled()) {
                log.debug("Filtered offer from station with unknown landing pad: {}", offerDetail);
            }
            return result;
        }

        boolean result = landingPad.isLargeEnough(request.getMinLandingPad());
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtered offer from station with too small landing pad: {}", offerDetail);
        }
        return result;
    }
}
