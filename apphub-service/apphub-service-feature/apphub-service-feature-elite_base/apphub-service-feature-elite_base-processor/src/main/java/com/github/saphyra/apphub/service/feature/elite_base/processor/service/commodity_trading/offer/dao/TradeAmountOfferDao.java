package com.github.saphyra.apphub.service.feature.elite_base.processor.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.column.QualifiedColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.feature.elite_base.processor.common.DatabaseConstants.*;

@Component
@Slf4j
class TradeAmountOfferDao extends OfferDaoBase {
    TradeAmountOfferDao(OfferDaoContext context) {
        super(context);
    }

    @Override
    protected QualifiedColumn getOrderByColumn(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> new QualifiedColumn(TABLE_ITEM_COMMODITY_V2, COLUMN_STOCK);
            case SELL -> new QualifiedColumn(TABLE_ITEM_COMMODITY_V2, COLUMN_DEMAND);
        };
    }

    @Override
    public OrderCommoditiesBy getOrderBy() {
        return OrderCommoditiesBy.TRADE_AMOUNT;
    }
}
