package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.Column;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_BUY_PRICE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_SELL_PRICE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_ITEM_COMMODITY;

@Component
@Slf4j
//TODO unit test
class PriceOfferDao extends OfferDaoBase {
    PriceOfferDao(OfferDaoContext context) {
        super(context);
    }

    @Override
    protected Column getOrderByColumn(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_SELL_PRICE);
            case SELL -> new QualifiedColumn(TABLE_ITEM_COMMODITY, COLUMN_BUY_PRICE);
        };
    }

    @Override
    public OrderCommoditiesBy getOrderBy() {
        return OrderCommoditiesBy.PRICE;
    }
}
