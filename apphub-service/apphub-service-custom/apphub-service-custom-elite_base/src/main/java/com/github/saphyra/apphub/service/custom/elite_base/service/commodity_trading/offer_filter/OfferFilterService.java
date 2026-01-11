package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Builder
@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class OfferFilterService {
    private final List<OfferFilter> filters;
    private final LastUpdatedOfferFilter lastUpdatedOfferFilter;

    public List<OfferDetail> filterOffers(List<OfferDetail> offers, CommodityTradingRequest request) {
        log.debug("Offers found: {}", offers.size());

        List<OfferDetail> result = offers.stream()
            .filter(response -> filters.stream().allMatch(offerFilter -> offerFilter.matches(response, request)))
            .toList();

        result = lastUpdatedOfferFilter.filter(result, request);
        log.debug("Remaining offers after filtering: {}", result.size());

        return result;
    }
}
