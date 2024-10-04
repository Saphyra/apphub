package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import io.restassured.response.Response;

import java.util.UUID;

public class SkyXploreGameDataActions {
    public static Response getGameDateResponse(int serverPort, UUID accessTokenId, String dataId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_ITEM_DATA, "dataId", dataId));
    }
}
