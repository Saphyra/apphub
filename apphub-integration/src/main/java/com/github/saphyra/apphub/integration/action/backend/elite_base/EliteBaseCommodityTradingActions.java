package com.github.saphyra.apphub.integration.action.backend.elite_base;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.integration.structure.api.elite_base.CommodityTradingRequest;
import io.restassured.response.Response;

import java.util.UUID;

public class EliteBaseCommodityTradingActions {
    public static Response getCommoditiesResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_COMMODITY_TRADING_COMMODITIES));
    }

    public static Response getBestTradeLocationsResponse(int serverPort, String accessToken, CommodityTradingRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_COMMODITY_TRADING_TRADE));
    }

    public static Response getCommodityAveragePriceResponse(int serverPort, String accessToken, String commodityName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_COMMODITY_TRADING_COMMODITIES_AVERAGE_PRICE, "commodityName", commodityName));
    }
}
