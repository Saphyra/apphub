package com.github.saphyra.apphub.service.feature.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.type.ItemTypeDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommodityTradingControllerImplTest {
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer AVERAGE_PRICE = 23;

    @Mock
    private CommodityTradingService commodityTradingService;

    @Mock
    private ItemTypeDao itemTypeDao;

    @Mock
    private MonitoringInstruments monitoringInstruments;

    @Mock
    private CommodityAveragePriceDao commodityAveragePriceDao;

    @InjectMocks
    private CommodityTradingControllerImpl underTest;

    @Mock
    private CommodityTradingRequest request;

    @Mock
    private CommodityTradingResponse response;

    @Mock
    private AccessToken accessToken;

    @Mock
    private CommodityAveragePrice commodityAveragePrice;

    @Test
    void bestTradeLocations() {
        given(commodityTradingService.getTradeOffers(request)).willReturn(response);
        given(monitoringInstruments.wrap(any(Supplier.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Supplier.class).get());

        assertThat(underTest.bestTradeLocations(request, accessToken)).isEqualTo(response);
    }

    @Test
    void getCommodities() {
        given(itemTypeDao.getItemNames(ObjectType.TRADING_TYPES)).willReturn(List.of(COMMODITY_NAME));

        assertThat(underTest.getCommodities(accessToken)).containsExactly(COMMODITY_NAME);
    }

    @Test
    void getCommodityAveragePrice() {
        given(commodityAveragePriceDao.findByIdValidated(COMMODITY_NAME)).willReturn(commodityAveragePrice);
        given(commodityAveragePrice.getAveragePrice()).willReturn(AVERAGE_PRICE);

        assertThat(underTest.getCommodityAveragePrice(COMMODITY_NAME, accessToken)).isEqualTo(AVERAGE_PRICE);
    }
}