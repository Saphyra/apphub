package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import io.restassured.response.Response;

import java.util.UUID;

public class SkyXploreGameDataActions {
    public static Response getGameDateResponse(Language language, UUID accessTokenId, String dataId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_ITEM_DATA, "dataId", dataId));
    }
}
