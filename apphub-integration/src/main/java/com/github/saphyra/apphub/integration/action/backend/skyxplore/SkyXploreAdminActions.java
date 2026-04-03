package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreAdminEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.GameItemType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

public class SkyXploreAdminActions {
    public static Response getGetByTypeResponse(int serverPort, UUID accessTokenId, GameItemType type, UUID gameId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_GET_BY_TYPE, Map.of("type", type), Map.of("gameId", gameId)));
    }

    public static Response getGetItemResponse(int serverPort, UUID accessTokenId, UUID gameId, GameItemType type, UUID itemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_GET_ITEM, Map.of("type", type, "gameId", gameId, "itemId", itemId)));
    }
}
