package com.github.saphyra.apphub.integration.backend.actions.admin_panel;

import com.github.saphyra.apphub.integration.backend.model.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleActions {
    public static List<DisabledRoleResponse> getDisabledRoles(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.USER_DATA_GET_DISABLED_ROLES));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(DisabledRoleResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getDisableRoleResponse(Language language, UUID accessTokenId, String password, String role) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(password))
            .put(UrlFactory.create(Endpoints.USER_DATA_DISABLE_ROLE, "role", role));
    }

    public static Response getEnableRoleResponse(Language language, UUID accessTokenId, String password, String role) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(password))
            .delete(UrlFactory.create(Endpoints.USER_DATA_DISABLE_ROLE, "role", role));
    }
}
