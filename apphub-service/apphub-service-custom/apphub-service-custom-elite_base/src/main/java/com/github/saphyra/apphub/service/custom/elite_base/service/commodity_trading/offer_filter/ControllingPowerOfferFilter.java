package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
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
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
        boolean result = request.getControllingPowerRelation()
            .apply(
                request.getControllingPowers(),
                Optional.ofNullable(response.getControllingPower())
                    .map(List::of)
                    .orElse(Collections.emptyList())
            );
        if (!result) {
            log.info("Filtered offer with incorrect controllingFaction: {}", response);
        }
        return result;
    }
}
