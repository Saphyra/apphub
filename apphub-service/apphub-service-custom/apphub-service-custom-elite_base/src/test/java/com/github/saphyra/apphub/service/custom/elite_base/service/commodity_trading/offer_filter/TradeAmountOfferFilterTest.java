package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TradeAmountOfferFilterTest {
    private static final Integer MIN_TRADE_AMOUNT = 3214;

    @InjectMocks
    private TradeAmountOfferFilter underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @Test
    void tradeAmountEnough() {
        given(request.getMinTradeAmount()).willReturn(MIN_TRADE_AMOUNT);
        given(response.getTradeAmount()).willReturn(MIN_TRADE_AMOUNT);

        assertThat(underTest.matches(response, request)).isTrue();
    }

    @Test
    void tradeAmountTooLow() {
        given(request.getMinTradeAmount()).willReturn(MIN_TRADE_AMOUNT);
        given(response.getTradeAmount()).willReturn(MIN_TRADE_AMOUNT - 1);

        assertThat(underTest.matches(response, request)).isFalse();
    }
}