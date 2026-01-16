package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import org.springframework.core.Ordered;

import java.util.List;

public interface OfferFilter extends Ordered {
    List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request);
}
