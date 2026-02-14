package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_BATCH_SIZE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_THREAD_COUNT;

@Builder
@Component
@Slf4j
@RequiredArgsConstructor
public class OfferFilterService {
    private final List<OfferFilter> filters;
    private final ExecutorServiceBean executorServiceBean;

    public List<OfferDetail> filterOffers(List<OfferDetail> offers, CommodityTradingRequest request) {
        log.debug("Offers found: {}", offers.size());

        List<OfferDetail> result = offers;
        for (OfferFilter filter : filters) {
            result = executorServiceBean.processBatch(
                result,
                o -> filter.filter(o, request),
                COMMODITY_TRADING_BATCH_SIZE,
                COMMODITY_TRADING_THREAD_COUNT
            );
            log.info("Remaining offers after running {}: {}", filter.getClass().getSimpleName(), result.size());
        }

        log.debug("Remaining offers after filtering: {}", result.size());

        return result;
    }
}
