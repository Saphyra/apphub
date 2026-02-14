package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail.OfferDetail;

import java.util.List;

public interface OfferFilter {
    List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request);
}
