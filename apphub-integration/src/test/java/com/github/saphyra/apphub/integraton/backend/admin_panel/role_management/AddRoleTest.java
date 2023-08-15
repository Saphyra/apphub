package com.github.saphyra.apphub.integraton.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "admin-panel"})
    public void addRole(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID userId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullUserId(language, accessTokenId);
        blankRole(language, accessTokenId);
        userNotFound(language, accessTokenId);
        roleAlreadyExists(language, accessTokenId, userId);
        addRole(language, accessTokenId, testUser, userId);
    }

    private static void nullUserId(Language language, UUID accessTokenId) {
        RoleRequest nullUserIdRequest = RoleRequest.builder()
            .userId(null)
            .role(Constants.ROLE_ADMIN)
            .build();
        Response nullUserIdResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, nullUserIdRequest);
        verifyInvalidParam(language, nullUserIdResponse, "userId", "must not be null");
    }

    private static void blankRole(Language language, UUID accessTokenId) {
        RoleRequest blankRoleRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(" ")
            .build();
        Response blankRoleResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, blankRoleRequest);
        verifyInvalidParam(language, blankRoleResponse, "role", "must not be null or blank");
    }

    private static void userNotFound(Language language, UUID accessTokenId) {
        RoleRequest userNotFoundRequest = RoleRequest.builder()
            .userId(UUID.randomUUID())
            .role(Constants.ROLE_ADMIN)
            .build();
        Response userNotFoundResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, userNotFoundRequest);
        verifyErrorResponse(language, userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void roleAlreadyExists(Language language, UUID accessTokenId, UUID userId) {
        RoleRequest roleAlreadyExistsRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_NOTEBOOK)
            .build();
        Response roleAlreadyExistsResponse = RoleManagementActions.getAddRoleResponse(language, accessTokenId, roleAlreadyExistsRequest);
        verifyErrorResponse(language, roleAlreadyExistsResponse, 409, ErrorCode.ROLE_ALREADY_EXISTS);
    }

    private static void addRole(Language language, UUID accessTokenId, RegistrationParameters testUser, UUID userId) {
        RoleRequest addRoleRequest = RoleRequest.builder()
            .userId(userId)
            .role(Constants.ROLE_TEST)
            .build();
        RoleManagementActions.addRole(language, accessTokenId, addRoleRequest);
        List<UserRoleResponse> responses = RoleManagementActions.getRoles(language, accessTokenId, testUser.getUsername());
        assertThat(responses.get(0).getRoles()).contains(Constants.ROLE_TEST);
    }
}
