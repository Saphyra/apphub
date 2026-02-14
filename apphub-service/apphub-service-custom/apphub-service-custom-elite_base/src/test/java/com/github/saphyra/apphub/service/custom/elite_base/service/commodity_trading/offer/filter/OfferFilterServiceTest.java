package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;

import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_BATCH_SIZE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants.COMMODITY_TRADING_THREAD_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OfferFilterServiceTest {
    @Mock
    private OfferFilter offerFilter;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    private OfferFilterService underTest;

    @Mock
    private OfferDetail offerDetail;

    @Mock
    private CommodityTradingRequest request;

    @BeforeEach
    void setUp() {
        underTest = new OfferFilterService(List.of(offerFilter), executorServiceBean);
    }

    @Test
    void doNotFilter() {
        given(executorServiceBean.processBatch(eq(List.of(offerDetail)), any(), eq(COMMODITY_TRADING_BATCH_SIZE), eq(COMMODITY_TRADING_THREAD_COUNT))).willAnswer(invocation -> {
            return invocation.getArgument(1, Function.class)
                .apply(invocation.getArgument(0));
        });
        given(offerFilter.filter(List.of(offerDetail), request)).willReturn(List.of(offerDetail));

        assertThat(underTest.filterOffers(List.of(offerDetail), request)).containsExactly(offerDetail);
    }

    @Test
    void filter() {
        given(executorServiceBean.processBatch(eq(List.of(offerDetail)), any(), eq(COMMODITY_TRADING_BATCH_SIZE), eq(COMMODITY_TRADING_THREAD_COUNT))).willAnswer(invocation -> {
            return invocation.getArgument(1, Function.class)
                .apply(invocation.getArgument(0));
        });
        given(offerFilter.filter(List.of(offerDetail), request)).willReturn(List.of());

        assertThat(underTest.filterOffers(List.of(offerDetail), request)).isEmpty();
    }
}