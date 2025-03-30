package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class PowerplayStateOfferFilter implements OfferFilter {
    @Override
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
        if (isNull(request.getPowerplayState())) {
            //User does not filter for powerplayState
            return true;
        }

        boolean result = Optional.ofNullable(response.getPowerplayState())
            .filter(powerplayState -> powerplayState.equals(request.getPowerplayState()))
            .isPresent();
        if (!result) {
            log.info("Filtering offer from system with incorrect powerplayState {}", response);
        }
        return result;
    }
}
