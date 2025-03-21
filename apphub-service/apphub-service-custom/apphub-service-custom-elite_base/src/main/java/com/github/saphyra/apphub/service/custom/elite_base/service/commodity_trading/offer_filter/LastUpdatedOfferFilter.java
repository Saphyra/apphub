package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
class LastUpdatedOfferFilter implements OfferFilter {
    private final ExpirationCache expirationCache;

    @Override
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
            LocalDateTime expiration = expirationCache.getExpiration(request);

            boolean result = response.getLastUpdated().isAfter(expiration);
            if (!result) {
                log.info("Filtered offer based on expired data. Expiration: {}. {}", expiration, response);
            }
            return result;
    }
}
