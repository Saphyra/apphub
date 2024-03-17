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
        nullPassword(accessTokenId, userId);
        userNotFound(accessTokenId, userData);
        incorrectPassword(accessTokenId, userId);
        roleAlreadyExists(accessTokenId, userData, userId);
        addRole(accessTokenId, userData, testUser, userId);
    }

    private void incorrectPassword(UUID accessTokenId, UUID userId) {
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response response = RoleManagementActions.getAddRoleResponse(accessTokenId, roleAlreadyExistsRequest);
        verifyErrorResponse(response, 400, ErrorCode.INCORRECT_PASSWORD);
    }

    private void nullPassword(UUID accessTokenId, UUID userId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .password(null)
            .build();
        Response response = RoleManagementActions.getAddRoleResponse(accessTokenId, nullUserIdRequest);
        verifyInvalidParam(response, "password", "must not be null");
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

    private static void userNotFound(UUID accessTokenId, RegistrationParameters userData) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .password(userData.getPassword())
            .build();
        Response userNotFoundResponse = RoleManagementActions.getAddRoleResponse(accessTokenId, userNotFoundRequest);
        verifyErrorResponse(userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleAlreadyExists(UUID accessTokenId, RegistrationParameters userData, UUID userId) {
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(userData.getPassword())
            .build();
        Response roleAlreadyExistsResponse = RoleManagementActions.getAddRoleResponse(accessTokenId, roleAlreadyExistsRequest);
        verifyErrorResponse(roleAlreadyExistsResponse, 409, ErrorCode.ROLE_ALREADY_EXISTS);
    }

    private static void addRole(UUID accessTokenId, RegistrationParameters userData, RegistrationParameters testUser, UUID userId) {
        RoleRequest addRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_TEST)
            .password(userData.getPassword())
            .build();
        UserRoleResponse userRoleResponse = RoleManagementActions.addRole(accessTokenId, addRoleRequest);
        assertThat(userRoleResponse.getRoles()).contains(Constants.ROLE_TEST);
    }
}
