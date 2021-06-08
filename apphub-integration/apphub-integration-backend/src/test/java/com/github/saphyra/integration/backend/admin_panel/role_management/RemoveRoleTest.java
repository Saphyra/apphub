package com.github.saphyra.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.backend.model.RoleRequest;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.UserRoleResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveRoleTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void removeRole(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        //Null userId
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getRemoveRoleResponse(language, accessTokenId, nullUserIdRequest);
        verifyInvalidParam(language, nullUserIdResponse, "userId", "must not be null");

        //Blank role
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getRemoveRoleResponse(language, accessTokenId, blankRoleRequest);
        verifyInvalidParam(language, blankRoleResponse, "role", "must not be null or blank");

        //User not found
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .build();
        Response userNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(language, accessTokenId, userNotFoundRequest);
        verifyResponse(language, userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);

        //Role not found
        RoleRequest roleNotFoundRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response roleNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(language, accessTokenId, roleNotFoundRequest);
        verifyResponse(language, roleNotFoundResponse, 404, ErrorCode.ROLE_NOT_FOUND);

        //Remove role
        RoleRequest removeRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .build();
        RoleManagementActions.removeRole(language, accessTokenId, removeRoleRequest);

        List<UserRoleResponse> responses = RoleManagementActions.getRoles(language, accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).doesNotContain(Constants.ROLE_NOTEBOOK);
    }

    private void verifyInvalidParam(Language language, Response nullUserIdResponse, String field, String value) {
        ErrorResponse errorResponse = verifyResponse(language, nullUserIdResponse, 400, ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }

    private ErrorResponse verifyResponse(Language language, Response nullUserIdResponse, int httpStatus, ErrorCode errorCode) {
        assertThat(nullUserIdResponse.getStatusCode()).isEqualTo(httpStatus);
        ErrorResponse errorResponse = nullUserIdResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.fromErrorCode(errorCode)));
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        return errorResponse;
    }
}
