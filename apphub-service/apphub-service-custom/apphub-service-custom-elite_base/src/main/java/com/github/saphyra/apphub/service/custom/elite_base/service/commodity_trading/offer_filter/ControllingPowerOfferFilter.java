package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class ControllingPowerOfferFilter implements OfferFilter {
    @Override
    public boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        boolean result = request.getControllingPowerRelation()
            .apply(
                request.getControllingPowers(),
                Optional.ofNullable(offerDetail.getControllingPower())
                    .map(Enum::name)
                    .map(List::of)
                    .orElse(Collections.emptyList())
            );
        if (!result) {
            log.debug("Filtered offer with incorrect controllingFaction: {}", offerDetail);
        }
        return result;
    }
}
