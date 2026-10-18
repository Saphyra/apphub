package com.github.saphyra.apphub.service.feature.elite_base.processor.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.processor.service.commodity_trading.offer.Offer;

import java.util.List;

public interface OfferDao {
    OrderCommoditiesBy getOrderBy();

    List<Offer> getOffers(int offset, CommodityTradingRequest request, StarSystem referenceSystem);
}
