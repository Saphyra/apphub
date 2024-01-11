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

public class AddRoleTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    public void addRole() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullUserId(accessTokenId);
        blankRole(accessTokenId);
        userNotFound(accessTokenId);
        roleAlreadyExists(accessTokenId, userId);
        addRole(accessTokenId, testUser, userId);
    }

    private static void nullUserId(UUID accessTokenId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getAddRoleResponse(accessTokenId, nullUserIdRequest);
        verifyInvalidParam(nullUserIdResponse, "userId", "must not be null");
    }

    private static void blankRole(UUID accessTokenId) {
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getAddRoleResponse(accessTokenId, blankRoleRequest);
        verifyInvalidParam(blankRoleResponse, "role", "must not be null or blank");
    }

    private static void userNotFound(UUID accessTokenId) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .build();
        Response userNotFoundResponse = RoleManagementActions.getAddRoleResponse(accessTokenId, userNotFoundRequest);
        verifyErrorResponse(userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleAlreadyExists(UUID accessTokenId, UUID userId) {
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .build();
        Response roleAlreadyExistsResponse = RoleManagementActions.getAddRoleResponse(accessTokenId, roleAlreadyExistsRequest);
        verifyErrorResponse(roleAlreadyExistsResponse, 409, ErrorCode.ROLE_ALREADY_EXISTS);
    }

    private static void addRole(UUID accessTokenId, RegistrationParameters testUser, UUID userId) {
        RoleRequest addRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_TEST)
            .build();
        RoleManagementActions.addRole(accessTokenId, addRoleRequest);
        List<UserRoleResponse> responses = RoleManagementActions.getRoles(accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).contains(Constants.ROLE_TEST);
    }
}
