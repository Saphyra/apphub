package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleActions {
    public static List<DisabledRoleResponse> getDisabledRoles(int serverPort, String accessToken) {
        Response response = getGetDisabledRoles(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(DisabledRoleResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getGetDisabledRoles(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, UserEndpoints.USER_DATA_GET_DISABLED_ROLES));
    }

    public static Response getDisableRoleResponse(int serverPort, String accessToken, String password, String role) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(password))
            .put(UrlFactory.create(serverPort, UserEndpoints.USER_DATA_DISABLE_ROLE, "role", role));
    }

    public static Response getEnableRoleResponse(int serverPort, String accessToken, String password, String role) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(password))
            .delete(UrlFactory.create(serverPort, UserEndpoints.USER_DATA_DISABLE_ROLE, "role", role));
    }
}
