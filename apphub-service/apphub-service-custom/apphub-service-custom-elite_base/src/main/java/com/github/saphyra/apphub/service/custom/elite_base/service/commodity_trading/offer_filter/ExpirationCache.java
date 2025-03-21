package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ExpirationCache extends ThreadLocal<LocalDateTime> {
    private final DateTimeUtil dateTimeUtil;

    LocalDateTime getExpiration(CommodityTradingRequest request) {
        return Optional.ofNullable(get())
            .orElseGet(() -> {
                LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
                    .minus(request.getMaxTimeSinceLastUpdated());
                set(expiration);
                return expiration;
            });
    }
}
