package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreAdminEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.GameItemType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

public class SkyXploreAdminActions {
    public static Response getGetByTypeResponse(int serverPort, String accessToken, GameItemType type, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_GET_BY_TYPE, Map.of("type", type), Map.of("gameId", gameId)));
    }

    public static Response getGetItemResponse(int serverPort, String accessToken, UUID gameId, GameItemType type, UUID itemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_GET_ITEM, Map.of("type", type, "gameId", gameId, "itemId", itemId)));
    }
}
