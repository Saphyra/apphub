package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SystemDistanceOfferFilter implements OfferFilter {
    @Override
    public boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        boolean result = offerDetail.getDistanceFromReferenceSystem() <= request.getMaxStarSystemDistance();
        if (!result) {
            log.debug("Filtered offer because system is too far: {}", offerDetail);
        }
        return result;
    }
}
