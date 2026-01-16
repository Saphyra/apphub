package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.OfferDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class TradeAmountOfferFilter implements OfferFilter {
    @Override
    public List<OfferDetail> filter(List<OfferDetail> offers, CommodityTradingRequest request) {
        return offers.stream()
            .filter(offerDetail -> matches(offerDetail, request))
            .toList();
    }

    private boolean matches(OfferDetail offerDetail, CommodityTradingRequest request) {
        boolean result = offerDetail.getTradingItem().getTradeAmount(offerDetail.getTradeMode()) >= request.getMinTradeAmount();
        if (!result && log.isDebugEnabled()) {
            log.debug("Filtering offer with amount too low {}", offerDetail);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return OfferFilterOrder.DEFAULT_ORDER.getOrder();
    }
}
