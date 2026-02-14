package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE;

@Component
@Slf4j
class LastUpdatedOfferDao extends OfferDaoBase {
    LastUpdatedOfferDao(OfferDaoContext context) {
        super(context);
    }

    @Override
    protected QualifiedColumn getOrderByColumn(TradeMode tradeMode) {
        return new QualifiedColumn(TABLE_LAST_UPDATE, COLUMN_LAST_UPDATE);
    }

    @Override
    public OrderCommoditiesBy getOrderBy() {
        return OrderCommoditiesBy.LAST_UPDATED;
    }
}
