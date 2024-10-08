package com.github.saphyra.apphub.integration.action.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.ModulesResponse;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ModulesActions {
    public static void logout(int serverPort, UUID accessTokenId) {
        Response response = getLogoutResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getLogoutResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, UserEndpoints.LOGOUT));
    }

    public static Map<String, List<ModulesResponse>> getModules(int serverPort, UUID accessTokenId) {
        Response response = getModulesResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, List<ModulesResponse>>> ref = new TypeReference<>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getModulesResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_GET_MODULES_OF_USER));
    }

    public static Map<String, List<ModulesResponse>> setAsFavorite(int serverPort, UUID accessTokenId, String module, Boolean favorite) {
        Response response = getSetAsFavoriteResponse(serverPort, accessTokenId, module, favorite);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, List<ModulesResponse>>> ref = new TypeReference<>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getSetAsFavoriteResponse(int serverPort, UUID accessTokenId, String module, Boolean favorite) {
        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("module", module);

        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(favorite))
            .post(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_SET_FAVORITE, pathVariables));
    }
}
