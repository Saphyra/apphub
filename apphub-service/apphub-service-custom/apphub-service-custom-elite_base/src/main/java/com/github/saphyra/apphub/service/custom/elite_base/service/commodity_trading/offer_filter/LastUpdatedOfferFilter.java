package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LastUpdatedOfferFilter implements OfferFilter {
    private static final ThreadLocal<LocalDateTime> CURRENT_TIME = new ThreadLocal<>();

    private final DateTimeUtil dateTimeUtil;

    @Override
    public boolean matches(CommodityTradingResponse response, CommodityTradingRequest request) {
        try {
            LocalDateTime expiration = getCurrentTime()
                .minus(request.getMaxTimeSinceLastUpdated());

            boolean result = response.getLastUpdated().isAfter(expiration);
            if (!result) {
                log.info("Filtered offer based on expired data. Expiration: {}. {}", expiration, response);
            }
            return result;
        } finally {
            CURRENT_TIME.remove();
        }
    }

    private LocalDateTime getCurrentTime() {
        return Optional.ofNullable(CURRENT_TIME.get())
            .orElseGet(() -> {
                LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
                CURRENT_TIME.set(currentTime);
                return currentTime;
            });
    }
}
