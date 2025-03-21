package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Builder
@Component
@Slf4j
@RequiredArgsConstructor
public class OfferFilterService {
    private final List<OfferFilter> filters;
    private final ExpirationCache expirationCache;

    public List<CommodityTradingResponse> filterOffers(List<CommodityTradingResponse> offers, CommodityTradingRequest request) {
        try {
            log.info("Offers found: {}", offers.size());

            List<CommodityTradingResponse> result = offers
                .stream()
                .filter(response -> filters.stream().allMatch(offerFilter -> offerFilter.matches(response, request)))
                .toList();
            log.info("Remaining offers after filtering: {}", result.size());

            return result;
        } finally {
            expirationCache.remove();
        }
    }
}
