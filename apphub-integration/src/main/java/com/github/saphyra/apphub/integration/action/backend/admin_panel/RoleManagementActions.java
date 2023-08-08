package com.github.saphyra.apphub.integration.action.backend.admin_panel;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
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
    public static List<UserRoleResponse> getRoles(Language language, UUID accessTokenId, String queryString) {
        Response response = getRolesResponse(language, accessTokenId, queryString);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(UserRoleResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getRolesResponse(Language language, UUID accessTokenId, String queryString) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(Endpoints.USER_DATA_GET_USER_ROLES));
    }

    public static void addRole(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        Response response = getAddRoleResponse(language, accessTokenId, roleRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAddRoleResponse(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(roleRequest)
            .put(UrlFactory.create(Endpoints.USER_DATA_ADD_ROLE));
    }

    public static void removeRole(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        Response response = getRemoveRoleResponse(language, accessTokenId, roleRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveRoleResponse(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(roleRequest)
            .delete(UrlFactory.create(Endpoints.USER_DATA_REMOVE_ROLE));
    }
}
