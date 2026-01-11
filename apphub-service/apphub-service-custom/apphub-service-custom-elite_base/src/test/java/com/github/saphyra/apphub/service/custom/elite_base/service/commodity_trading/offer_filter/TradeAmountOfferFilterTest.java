package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.TradeMode;
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
    private OfferDetail offerDetail;

    @Mock
    private Tradeable tradeItem;

    @Test
    void tradeAmountEnough() {
        given(request.getMinTradeAmount()).willReturn(MIN_TRADE_AMOUNT);
        given(offerDetail.getTradeMode()).willReturn(TradeMode.BUY);
        given(offerDetail.getTradingItem()).willReturn(tradeItem);
        given(tradeItem.getTradeAmount(TradeMode.BUY)).willReturn(MIN_TRADE_AMOUNT);

        assertThat(underTest.matches(offerDetail, request)).isTrue();
    }

    @Test
    void tradeAmountTooLow() {
        given(request.getMinTradeAmount()).willReturn(MIN_TRADE_AMOUNT);
        given(offerDetail.getTradeMode()).willReturn(TradeMode.BUY);
        given(offerDetail.getTradingItem()).willReturn(tradeItem);
        given(tradeItem.getTradeAmount(TradeMode.BUY)).willReturn(MIN_TRADE_AMOUNT - 1);

        assertThat(underTest.matches(offerDetail, request)).isFalse();
    }
}