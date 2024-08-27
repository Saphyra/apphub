package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.RoleRequest;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullUserId(accessTokenId);
        blankRole(accessTokenId);
        nullPassword(accessTokenId, userId);
        userNotFound(accessTokenId, userData);
        roleNotFound(accessTokenId, userData, userId);
        incorrectPassword(accessTokenId, userId);
        removeRole(accessTokenId, userData, userId);
    }

    private void incorrectPassword(UUID accessTokenId, UUID userId) {
        RoleRequest removeRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response response = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessTokenId, removeRoleRequest);
        verifyErrorResponse(response, 400, ErrorCode.INCORRECT_PASSWORD);
    }

    private void nullPassword(UUID accessTokenId, UUID userId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .password(null)
            .build();
        Response response = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessTokenId, nullUserIdRequest);
        verifyInvalidParam(response, "password", "must not be null");
    }

    private static void nullUserId(UUID accessTokenId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessTokenId, nullUserIdRequest);
        verifyInvalidParam(nullUserIdResponse, "userId", "must not be null");
    }

    private static void blankRole(UUID accessTokenId) {
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessTokenId, blankRoleRequest);
        verifyInvalidParam(blankRoleResponse, "role", "must not be null or blank");
    }

    private static void userNotFound(UUID accessTokenId, RegistrationParameters userData) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .password(userData.getPassword())
            .build();
        Response userNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessTokenId, userNotFoundRequest);
        verifyErrorResponse(userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleNotFound(UUID accessTokenId, RegistrationParameters userData, UUID userId) {
        RoleRequest roleNotFoundRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .password(userData.getPassword())
            .build();
        Response roleNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessTokenId, roleNotFoundRequest);
        verifyErrorResponse(roleNotFoundResponse, 404, ErrorCode.ROLE_NOT_FOUND);
    }

    private static void removeRole(UUID accessTokenId, RegistrationParameters userData, UUID userId) {
        RoleRequest removeRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(userData.getPassword())
            .build();
        UserRoleResponse userRoleResponse = RoleManagementActions.removeRole(getServerPort(), accessTokenId, removeRoleRequest);

        assertThat(userRoleResponse.getRoles()).doesNotContain(Constants.ROLE_NOTEBOOK);
    }
}
