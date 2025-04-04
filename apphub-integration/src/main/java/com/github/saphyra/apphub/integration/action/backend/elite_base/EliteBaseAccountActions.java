package com.github.saphyra.apphub.integration.action.backend.elite_base;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import io.restassured.response.Response;

import java.util.UUID;

public class EliteBaseAccountActions {
    public static Response getIsAdminResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_IS_ADMIN));
    }
}
