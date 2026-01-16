package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_BATCH_SIZE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_THREAD_COUNT;

@Builder
@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class OfferFilterService {
    private final List<OfferFilter> filters;
    private final ExecutorServiceBean executorServiceBean;

    public List<OfferDetail> filterOffers(List<OfferDetail> offers, CommodityTradingRequest request) {
        log.debug("Offers found: {}", offers.size());


        List<OfferDetail> result = offers;
        for (OfferFilter filter : filters.stream().sorted(Comparator.comparingInt(Ordered::getOrder)).toList()) {
            result = executorServiceBean.processBatch(
                offers,
                o -> filter.filter(o, request),
                COMMODITY_TRADING_BATCH_SIZE,
                COMMODITY_TRADING_THREAD_COUNT
            );
        }

        log.debug("Remaining offers after filtering: {}", result.size());

        return result;
    }
}
