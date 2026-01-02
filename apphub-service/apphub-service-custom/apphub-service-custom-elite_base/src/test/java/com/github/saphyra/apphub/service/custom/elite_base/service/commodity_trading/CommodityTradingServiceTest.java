package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter.OfferFilterService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommodityTradingServiceTest {
    private static final UUID REFERENCE_STAR_ID = UUID.randomUUID();
    private static final String COMMODITY = "commodity";
    private static final Integer MIN_TRADE_AMOUNT = 2543;
    private static final Integer MIN_PRICE = 45;
    private static final Integer MAX_PRICE = 4567;

    @Mock
    private CommodityTradingRequestValidator commodityTradingRequestValidator;

    @Mock
    private CommodityDao commodityDao;

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private OfferFilterService offerFilterService;

    @Mock
    private OfferDetailsFetcher offerDetailsFetcher;

    @Mock
    private PerformanceReporter performanceReporter;

    @InjectMocks
    private CommodityTradingService underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private CommodityTradingResponse filteredResponse;

    @Mock
    private Commodity commodity;

    @Mock
    private StarSystem starSystem;

    @Test
    void invalidTradeMode() {
        given(request.getTradeMode()).willReturn("asd");

        ExceptionValidator.validateInvalidParam(() -> underTest.getTradeOffers(request), "tradeMode", "invalid value");

        then(commodityTradingRequestValidator).should().validate(request);
    }

    @Test
    void getTradeOffers() {
        given(request.getTradeMode()).willReturn(TradeMode.SELL.name());
        given(request.getReferenceStarId()).willReturn(REFERENCE_STAR_ID);
        given(request.getCommodity()).willReturn(COMMODITY);
        given(request.getMinTradeAmount()).willReturn(MIN_TRADE_AMOUNT);
        given(request.getMinPrice()).willReturn(MIN_PRICE);
        given(request.getMaxPrice()).willReturn(MAX_PRICE);
        given(request.getIncludeFleetCarriers()).willReturn(true);

        given(starSystemDao.findByIdValidated(REFERENCE_STAR_ID)).willReturn(starSystem);
        given(commodityDao.findConsumers(COMMODITY, MIN_TRADE_AMOUNT, MIN_PRICE, MAX_PRICE)).willReturn(List.of(commodity));
        given(offerDetailsFetcher.assembleResponses(TradeMode.SELL, starSystem, List.of(commodity), true)).willReturn(List.of(response));
        given(offerFilterService.filterOffers(List.of(response), request)).willReturn(List.of(filteredResponse));
        given(performanceReporter.wrap(any(Callable.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());

        assertThat(underTest.getTradeOffers(request)).containsExactly(filteredResponse);

        then(commodityTradingRequestValidator).should().validate(request);
    }
}