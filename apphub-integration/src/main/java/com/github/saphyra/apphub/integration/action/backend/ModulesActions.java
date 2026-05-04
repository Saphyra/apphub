package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.AuthorizationEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.structure.api.ModulesResponse;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;
import tools.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ModulesActions {
    public static void logout(int serverPort, String accessToken, String refreshToken) {
        Response response = getLogoutResponse(serverPort, accessToken, refreshToken);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getLogoutResponse(int serverPort, String accessToken, String refreshToken) {
        return RequestFactory.createAuthorizedRequest(accessToken, refreshToken)
            .post(UrlFactory.create(serverPort, AuthorizationEndpoints.LOGOUT));
    }

    public static Map<String, List<ModulesResponse>> getModules(int serverPort, String accessToken) {
        Response response = getModulesResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, List<ModulesResponse>>> ref = new TypeReference<>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getModulesResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_GET_MODULES_OF_USER));
    }

    public static Map<String, List<ModulesResponse>> setAsFavorite(int serverPort, String accessToken, String module, Boolean favorite) {
        Response response = getSetAsFavoriteResponse(serverPort, accessToken, module, favorite);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeReference<Map<String, List<ModulesResponse>>> ref = new TypeReference<>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getSetAsFavoriteResponse(int serverPort, String accessToken, String module, Boolean favorite) {
        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("module", module);

        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(favorite))
            .post(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_SET_FAVORITE, pathVariables));
    }
}
