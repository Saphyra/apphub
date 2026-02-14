package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.*;

@Component
@Slf4j
class TradeAmountOfferDao extends OfferDaoBase {
    TradeAmountOfferDao(OfferDaoContext context) {
        super(context);
    }

    @Override
    protected QualifiedColumn getOrderByColumn(TradeMode tradeMode) {
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
