package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class LastUpdatedOfferFilter {
    private final DateTimeUtil dateTimeUtil;

    public List<CommodityTradingResponse> filter(List<CommodityTradingResponse> responses, CommodityTradingRequest request) {
            LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
                .minus(request.getMaxTimeSinceLastUpdated());

            //TODO create a cache for CommodityName <> CommodityType, then collect the externalReferences of the results, so lastUpdated can be batch queried. Filter expiration, and add it to the response

            boolean result = response.getLastUpdated().isAfter(expiration);
            if (!result) {
                log.info("Filtered offer based on expired data. Expiration: {}. {}", expiration, response);
            }
            return result;
    }
}
