package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.backend.model.OneParamRequest;
import com.github.saphyra.apphub.integration.backend.model.RoleRequest;
import com.github.saphyra.apphub.integration.backend.model.UserRoleResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleManagementPageActions {
    public static List<UserRoleResponse> getRoles(Language language, UUID accessTokenId, String queryString) {
        Response response = getRolesResponse(language, accessTokenId, queryString);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(UserRoleResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getRolesResponse(Language language, UUID accessTokenId, String queryString) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(queryString))
            .post(UrlFactory.create(Endpoints.GET_ROLES));
    }

    public static void addRole(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        Response response = getAddRoleResponse(language, accessTokenId, roleRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAddRoleResponse(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(roleRequest)
            .put(UrlFactory.create(Endpoints.ADD_ROLE));
    }

    public static void removeRole(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        Response response = getRemoveRoleResponse(language, accessTokenId, roleRequest);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRemoveRoleResponse(Language language, UUID accessTokenId, RoleRequest roleRequest) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(roleRequest)
            .delete(UrlFactory.create(Endpoints.REMOVE_ROLE));
    }
}
