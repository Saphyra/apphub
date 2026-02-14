package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.TradeMode;
import com.github.saphyra.apphub.lib.sql_builder.Column;
import com.github.saphyra.apphub.lib.sql_builder.DefaultColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_DISTANCE_FROM_REFERENCE;

@Component
@Slf4j
class SystemDistanceOfferDao extends OfferDaoBase {
    SystemDistanceOfferDao(OfferDaoContext context) {
        super(context);
    }

    @Override
    protected Column getOrderByColumn(TradeMode tradeMode) {
        return new DefaultColumn(COLUMN_DISTANCE_FROM_REFERENCE);
    }

    @Override
    public OrderCommoditiesBy getOrderBy() {
        return OrderCommoditiesBy.SYSTEM_DISTANCE;
    }
}
