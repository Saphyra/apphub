package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class ControllingPowerOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        boolean result = request.getControllingPowerRelation()
            .apply(
                request.getControllingPowers(),
                () -> offerDetail.getControllingPower()
                    .map(Enum::name)
                    .map(List::of)
                    .orElse(Collections.emptyList())
            );
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtered offer with incorrect controllingFaction: {}", offerDetail);
        }
        return result;
    }
}
