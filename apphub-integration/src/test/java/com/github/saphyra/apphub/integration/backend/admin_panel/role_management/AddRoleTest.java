package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.feature_lock.Feature;
import com.github.saphyra.apphub.integration.core.feature_lock.FeatureLocked;
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

public class AddRoleTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.ROLE_TEST)
    public void addRole() {
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
        incorrectPassword(accessToken, userId);
        roleAlreadyExists(accessToken, userData, userId);
        addRole(accessToken, userData, userId);
    }

    private void incorrectPassword(String accessToken, UUID userId) {
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response response = RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, roleAlreadyExistsRequest);
        verifyErrorResponse(response, 400, ErrorCode.INCORRECT_PASSWORD);
    }

    private void nullPassword(String accessToken, UUID userId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_ADMIN)
            .password(null)
            .build();
        Response response = RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, nullUserIdRequest);
        verifyInvalidParam(response, "password", "must not be null");
    }

    private static void nullUserId(String accessToken) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, nullUserIdRequest);
        verifyInvalidParam(nullUserIdResponse, "userId", "must not be null");
    }

    private static void blankRole(String accessToken) {
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, blankRoleRequest);
        verifyInvalidParam(blankRoleResponse, "role", "must not be null or blank");
    }

    private static void userNotFound(String accessToken, RegistrationParameters userData) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .password(userData.getPassword())
            .build();
        Response userNotFoundResponse = RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, userNotFoundRequest);
        verifyErrorResponse(userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleAlreadyExists(String accessToken, RegistrationParameters userData, UUID userId) {
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .password(userData.getPassword())
            .build();
        Response roleAlreadyExistsResponse = RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, roleAlreadyExistsRequest);
        verifyErrorResponse(roleAlreadyExistsResponse, 409, ErrorCode.ROLE_ALREADY_EXISTS);
    }

    private static void addRole(String accessToken, RegistrationParameters userData, UUID userId) {
        RoleRequest addRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_TEST)
            .password(userData.getPassword())
            .build();
        UserRoleResponse userRoleResponse = RoleManagementActions.addRole(getServerPort(), accessToken, addRoleRequest);
        assertThat(userRoleResponse.getRoles()).contains(Constants.ROLE_TEST);
    }
}
