package com.github.saphyra.apphub.integration.action.backend.elite_base;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.EliteBaseEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

public class EliteBaseStarSystemActions {
    public static Response getSearchResponse(int serverPort, String accessToken, String query) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(query))
            .get(UrlFactory.create(serverPort, EliteBaseEndpoints.ELITE_BASE_STAR_SYSTEMS_SEARCH));
    }
}
