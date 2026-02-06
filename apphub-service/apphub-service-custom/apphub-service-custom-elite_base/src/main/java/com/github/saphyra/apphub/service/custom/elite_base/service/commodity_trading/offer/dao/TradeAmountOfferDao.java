package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.Column;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_DEMAND;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_STOCK;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_COMMODITY;

@Component
@Slf4j
//TODO unit test
class TradeAmountOfferDao extends OfferDaoBase {
    TradeAmountOfferDao(OfferDaoContext context) {
        super(context);
    }

    @Override
    protected Column getOrderByColumn(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_STOCK);
            case SELL -> new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_DEMAND);
        };
    }

    @Override
    public OrderCommoditiesBy getOrderBy() {
        return OrderCommoditiesBy.TRADE_AMOUNT;
    }
}
