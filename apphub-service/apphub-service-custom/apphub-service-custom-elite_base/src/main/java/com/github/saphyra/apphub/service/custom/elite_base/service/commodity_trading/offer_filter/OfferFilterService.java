package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
//TODO unit test
public class OfferFilterService {
    private final List<OfferFilter> filters;

    OfferFilterService(List<OfferFilter> filters) {
        this.filters = filters.stream()
            .sorted(Comparator.comparingInt(OfferFilter::getWeight))
            .toList();
    }

    public List<CommodityTradingResponse> filterOffers(List<CommodityTradingResponse> offers, CommodityTradingRequest request) {
        log.info("Offers found: {}", offers.size());

        List<CommodityTradingResponse> result = offers
            .stream()
            .filter(response -> filters.stream().allMatch(offerFilter -> offerFilter.matches(response, request)))
            .toList();
        log.info("Remaining offers after filtering: {}", result.size());

        return result;
    }
}
