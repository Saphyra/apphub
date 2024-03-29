package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleActions {
    public static List<DisabledRoleResponse> getDisabledRoles(UUID accessTokenId) {
        Response response = getGetDisabledRoles(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(DisabledRoleResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getGetDisabledRoles(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.USER_DATA_GET_DISABLED_ROLES));
    }

    public static Response getDisableRoleResponse(UUID accessTokenId, String password, String role) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(password))
            .put(UrlFactory.create(Endpoints.USER_DATA_DISABLE_ROLE, "role", role));
    }

    public static Response getEnableRoleResponse(UUID accessTokenId, String password, String role) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(password))
            .delete(UrlFactory.create(Endpoints.USER_DATA_DISABLE_ROLE, "role", role));
    }
}
