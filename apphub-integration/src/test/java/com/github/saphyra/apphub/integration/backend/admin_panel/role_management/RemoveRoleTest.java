package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.RoleRequest;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.UserRoleResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveRoleTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    public void removeRole() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullUserId(accessToken);
        blankRole(accessToken);
        nullPassword(accessToken, userId);
        userNotFound(accessToken, userData);
        roleNotFound(accessToken, userData, userId);
        incorrectPassword(accessToken, userId); //TODO test lockout
        removeRole(accessToken, userData, userId);
    }

    private void incorrectPassword(String accessToken, UUID userId) {
        RoleRequest removeRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response response = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, removeRoleRequest);
        verifyErrorResponse(response, 400, ErrorCode.INCORRECT_PASSWORD);
    }

    private void nullPassword(String accessToken, UUID userId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(null)
            .build();
        Response response = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, nullUserIdRequest);
        verifyInvalidParam(response, "password", "must not be null");
    }

    private static void nullUserId(String accessToken) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_NOTEBOOK)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, nullUserIdRequest);
        verifyInvalidParam(nullUserIdResponse, "userId", "must not be null");
    }

    private static void blankRole(String accessToken) {
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, blankRoleRequest);
        verifyInvalidParam(blankRoleResponse, "role", "must not be null or blank");
    }

    private static void userNotFound(String accessToken, RegistrationParameters userData) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_NOTEBOOK)
            .password(userData.getPassword())
            .build();
        Response userNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, userNotFoundRequest);
        verifyErrorResponse(userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleNotFound(String accessToken, RegistrationParameters userData, UUID userId) {
        RoleRequest roleNotFoundRequest = RoleRequest.builder()
            .userId(userId)
            .role("non-existing-role")
            .password(userData.getPassword())
            .build();
        Response roleNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, roleNotFoundRequest);
        verifyErrorResponse(roleNotFoundResponse, 404, ErrorCode.ROLE_NOT_FOUND);
    }

    private static void removeRole(String accessToken, RegistrationParameters userData, UUID userId) {
        RoleRequest removeRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(userData.getPassword())
            .build();
        UserRoleResponse userRoleResponse = RoleManagementActions.removeRole(getServerPort(), accessToken, removeRoleRequest);

        assertThat(userRoleResponse.getRoles()).doesNotContain(Constants.ROLE_NOTEBOOK);
    }
}
