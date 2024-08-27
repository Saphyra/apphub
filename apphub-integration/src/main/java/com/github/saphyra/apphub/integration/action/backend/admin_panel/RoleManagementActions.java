package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.RoleRequest;
import com.github.saphyra.apphub.integration.structure.api.user.UserRoleResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleManagementActions {
    public static List<UserRoleResponse> getRoles(int serverPort, UUID accessTokenId, String queryString) {
        Response response = getRolesResponse(serverPort, accessTokenId, queryString);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(UserRoleResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getRolesResponse(int serverPort, UUID accessTokenId, String queryString) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(serverPort, Endpoints.USER_DATA_GET_USER_ROLES));
    }

    public static UserRoleResponse addRole(int serverPort, UUID accessTokenId, RoleRequest roleRequest) {
        Response response = getAddRoleResponse(serverPort, accessTokenId, roleRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(UserRoleResponse.class);
    }

    public static Response getAddRoleResponse(int serverPort, UUID accessTokenId, RoleRequest roleRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(roleRequest)
            .put(UrlFactory.create(serverPort, Endpoints.USER_DATA_ADD_ROLE));
    }

    public static UserRoleResponse removeRole(int serverPort, UUID accessTokenId, RoleRequest roleRequest) {
        Response response = getRemoveRoleResponse(serverPort, accessTokenId, roleRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(UserRoleResponse.class);
    }

    public static Response getRemoveRoleResponse(int serverPort, UUID accessTokenId, RoleRequest roleRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(roleRequest)
            .delete(UrlFactory.create(serverPort, Endpoints.USER_DATA_REMOVE_ROLE));
    }

    public static Response getAddToAllResponse(int serverPort, UUID accessTokenId, String password, String role) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(password))
            .post(UrlFactory.create(serverPort, Endpoints.USER_DATA_ADD_ROLE_TO_ALL, "role", role));
    }

    public static Response getRemoveFromAllResponse(int serverPort, UUID accessTokenId, String password, String role) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(password))
            .post(UrlFactory.create(serverPort, Endpoints.USER_DATA_REMOVE_ROLE_FROM_ALL, "role", role));
    }
}
