package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.CommodityFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterial;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterialDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterialFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TradingDaoSupportTest {
    private static final Long MARKET_ID = 6465L;
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer BUY_PRICE = 123;
    private static final Integer SELL_PRICE = 456;
    private static final Integer DEMAND = 789;
    private static final Integer STOCK = 1011;

    @Mock
    private CommodityDao commodityDao;

    @Mock
    private FcMaterialDao fcMaterialDao;

    @Mock
    private CommodityFactory commodityFactory;

    @Mock
    private FcMaterialFactory fcMaterialFactory;

    @InjectMocks
    private TradingDaoSupport underTest;

    @Mock
    private Commodity commodity;

    @Mock
    private FcMaterial fcMaterial;

    @Test
    void getByMarketId() {
        given(commodityDao.getByMarketId(MARKET_ID)).willAnswer(_ -> List.of(commodity));

        assertThat(underTest.getByMarketId(ItemType.COMMODITY, MARKET_ID)).containsExactly(commodity);
    }

    @Test
    void getByMarketId_unsupported() {
        assertThat(catchThrowable(() -> underTest.getByMarketId(ItemType.EQUIPMENT, MARKET_ID))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteAll() {
        underTest.deleteAll(ItemType.COMMODITY, List.of(commodity));

        then(commodityDao).should().deleteAllTradeables(List.of(commodity));
    }

    @Test
    void deleteAll_unsupported() {
        assertThat(catchThrowable(() -> underTest.deleteAll(ItemType.EQUIPMENT, List.of()))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_commodity() {
        given(commodityFactory.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK)).willReturn(commodity);

        assertThat(underTest.create(ItemType.COMMODITY, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK)).isEqualTo(commodity);
    }

    @Test
    void create_fcMaterial() {
        given(fcMaterialFactory.create(ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK)).willReturn(fcMaterial);

        assertThat(underTest.create(ItemType.FC_MATERIAL, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK)).isEqualTo(fcMaterial);
    }

    @Test
    void create_unsupported() {
        assertThat(catchThrowable(() -> underTest.create(ItemType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, NAME, BUY_PRICE, SELL_PRICE, DEMAND, STOCK))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveAll() {
        List<Tradeable> commodities = List.of(commodity);

        underTest.saveAll(ItemType.COMMODITY, commodities);

        then(commodityDao).should().saveAll(commodities);
    }

    @Test
    void saveAll_unsupported() {
        assertThat(catchThrowable(() -> underTest.saveAll(ItemType.EQUIPMENT, List.of()))).isInstanceOf(IllegalArgumentException.class);
    }
}