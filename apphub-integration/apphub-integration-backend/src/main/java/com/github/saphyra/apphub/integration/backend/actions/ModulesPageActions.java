package com.github.saphyra.apphub.integration.backend.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.integration.backend.model.ModulesResponse;
import com.github.saphyra.apphub.integration.backend.model.OneParamRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.TestBase.OBJECT_MAPPER_WRAPPER;
import static org.assertj.core.api.Assertions.assertThat;

public class ModulesPageActions {
    public static void logout(UUID accessTokenId) {
        Response response = getLogoutResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getLogoutResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoints.LOGOUT));
    }

    public static Map<String, List<ModulesResponse>> getModules(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_MODULES_OF_USER));

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, List<ModulesResponse>>> ref = new TypeReference<Map<String, List<ModulesResponse>>>() {
        };
        return OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Map<String, List<ModulesResponse>> setAsFavorite(UUID accessTokenId, String module, Boolean favorite) {
        Response response = getSetAsFavoriteResponse(accessTokenId, module, favorite);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, List<ModulesResponse>>> ref = new TypeReference<Map<String, List<ModulesResponse>>>() {
        };
        return OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getSetAsFavoriteResponse(UUID accessTokenId, String module, Boolean favorite) {
        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("module", module);

        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(favorite))
            .post(UrlFactory.create(Endpoints.SET_FAVORITE, pathVariables));
    }
}
