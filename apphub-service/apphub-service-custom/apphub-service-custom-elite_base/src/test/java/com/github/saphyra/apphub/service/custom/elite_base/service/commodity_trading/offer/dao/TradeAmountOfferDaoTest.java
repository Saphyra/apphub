package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TradeAmountOfferDaoTest {
    @Mock
    private OfferDaoContext context;

    @InjectMocks
    private TradeAmountOfferDao underTest;

    @Test
    void getOrderByColumn() {
        assertThat(underTest.getOrderByColumn(TradeMode.BUY))
            .returns(TABLE_ITEM_COMMODITY, QualifiedColumn::getTable)
            .returns(COLUMN_STOCK, QualifiedColumn::getColumn);

        assertThat(underTest.getOrderByColumn(TradeMode.SELL))
            .returns(TABLE_ITEM_COMMODITY, QualifiedColumn::getTable)
            .returns(COLUMN_DEMAND, QualifiedColumn::getColumn);
    }

    @Test
    void getOrderBy() {
        assertThat(underTest.getOrderBy()).isEqualTo(OrderCommoditiesBy.TRADE_AMOUNT);
    }
}