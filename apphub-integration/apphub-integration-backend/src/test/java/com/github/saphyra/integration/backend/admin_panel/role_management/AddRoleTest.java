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

public class AddRoleTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void addRole(Language language) {
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
        Response nullUserIdResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, nullUserIdRequest);
        verifyInvalidParam(language, nullUserIdResponse, "userId", "must not be null");

        //Blank role
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, blankRoleRequest);
        verifyInvalidParam(language, blankRoleResponse, "role", "must not be null or blank");

        //User not found
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .build();
        Response userNotFoundResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, userNotFoundRequest);
        verifyResponse(language, userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);

        //Role already exists
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .build();
        Response roleAlreadyExistsResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, roleAlreadyExistsRequest);
        verifyResponse(language, roleAlreadyExistsResponse, 409, ErrorCode.ROLE_ALREADY_EXISTS);

        //Add role
        RoleRequest addRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_TEST)
            .build();
        RoleManagementActions.addRole(language, accessTokenId, addRoleRequest);
        List<UserRoleResponse> responses = RoleManagementActions.getRoles(language, accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).containsExactlyInAnyOrder(Constants.ROLE_TEST, Constants.ROLE_NOTEBOOK, Constants.ROLE_SKYXPLORE);
    }

    private void verifyInvalidParam(Language language, Response response, String field, String value) {
        ErrorResponse errorResponse = verifyResponse(language, response, 400, ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }

    private ErrorResponse verifyResponse(Language language, Response response, int httpStatus, ErrorCode errorCode) {
        assertThat(response.getStatusCode()).isEqualTo(httpStatus);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.fromErrorCode(errorCode)));
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        return errorResponse;
    }
}
