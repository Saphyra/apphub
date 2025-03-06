package com.github.saphyra.apphub.api.elite_base.server;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

//TODO Role protection test
public interface CommodityTradingController {
    @PostMapping(EliteBaseEndpoints.ELITE_BASE_COMMODITY_TRADING_BUY)
    List<CommodityTradingResponse> bestBuyLocations(@RequestBody CommodityTradingRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(EliteBaseEndpoints.ELITE_BASE_COMMODITY_TRADING_SELL)
    List<CommodityTradingResponse> bestSellLocations(@RequestBody CommodityTradingRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
