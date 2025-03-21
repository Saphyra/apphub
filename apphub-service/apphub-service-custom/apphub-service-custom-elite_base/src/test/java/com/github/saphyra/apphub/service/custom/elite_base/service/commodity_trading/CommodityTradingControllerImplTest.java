package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityTradingControllerImplTest {
    private static final String COMMODITY_NAME = "commodity-name";

    @Mock
    private CommodityTradingService commodityTradingService;

    @Mock
    private CommodityDao commodityDao;

    @Mock
    private PerformanceReporter performanceReporter;

    @InjectMocks
    private CommodityTradingControllerImpl underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    void bestTradeLocations() {
        given(performanceReporter.wrap(any(Callable.class), eq(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), eq(PerformanceReportingKey.API_BEST_TRADE_LOCATIONS.name())))
            .willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());
        given(commodityTradingService.getTradeOffers(request)).willReturn(List.of(response));

        assertThat(underTest.bestTradeLocations(request, accessTokenHeader)).containsExactly(response);
    }

    @Test
    void getCommodities() {
        given(commodityDao.getCommodities()).willReturn(List.of(COMMODITY_NAME));

        assertThat(underTest.getCommodities(accessTokenHeader)).containsExactly(COMMODITY_NAME);
    }
}