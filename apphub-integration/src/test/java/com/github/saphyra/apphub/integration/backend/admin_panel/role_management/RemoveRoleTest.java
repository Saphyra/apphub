package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.RoleRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.UserRoleResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveRoleTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    public void removeRole() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullUserId(accessTokenId);
        blankRole(accessTokenId);
        userNotFound(accessTokenId);
        roleNotFound(accessTokenId, userId);
        removeRole(accessTokenId, testUser, userId);
    }

    private static void nullUserId(UUID accessTokenId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getRemoveRoleResponse(accessTokenId, nullUserIdRequest);
        verifyInvalidParam(nullUserIdResponse, "userId", "must not be null");
    }

    private static void blankRole(UUID accessTokenId) {
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getRemoveRoleResponse(accessTokenId, blankRoleRequest);
        verifyInvalidParam(blankRoleResponse, "role", "must not be null or blank");
    }

    private static void userNotFound(UUID accessTokenId) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .build();
        Response userNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(accessTokenId, userNotFoundRequest);
        verifyErrorResponse(userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleNotFound(UUID accessTokenId, UUID userId) {
        RoleRequest roleNotFoundRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response roleNotFoundResponse = RoleManagementActions.getRemoveRoleResponse(accessTokenId, roleNotFoundRequest);
        verifyErrorResponse(roleNotFoundResponse, 404, ErrorCode.ROLE_NOT_FOUND);
    }

    private static void removeRole(UUID accessTokenId, RegistrationParameters testUser, UUID userId) {
        RoleRequest removeRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .build();
        RoleManagementActions.removeRole(accessTokenId, removeRoleRequest);

        List<UserRoleResponse> responses = RoleManagementActions.getRoles(accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).doesNotContain(Constants.ROLE_NOTEBOOK);
    }
}
