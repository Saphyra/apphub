package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.api.elite_base.server.CommodityTradingController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommodityTradingControllerImpl implements CommodityTradingController {
    private final CommodityTradingService commodityTradingService;

    @Override
    public List<CommodityTradingResponse> bestBuyLocations(CommodityTradingRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to find best buy locations based on {}", accessTokenHeader.getUserId(), request);

        return commodityTradingService.getTradeOffers(TradeMode.BUY, request);
    }

    @Override
    public List<CommodityTradingResponse> bestSellLocations(CommodityTradingRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to find best sell locations based on {}", accessTokenHeader.getUserId(), request);

        return commodityTradingService.getTradeOffers(TradeMode.SELL, request);
    }
}
