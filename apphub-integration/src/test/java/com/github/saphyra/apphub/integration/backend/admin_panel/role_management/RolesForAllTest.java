package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.feature_lock.Feature;
import com.github.saphyra.apphub.integration.core.feature_lock.FeatureLocked;
import com.github.saphyra.apphub.integration.framework.*;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.DataConstants.INCORRECT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

public class RolesForAllTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.ROLE_TEST)
    public void rolesForAll() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());

        addToAll_restrictedRole(accessTokenId, userData.getPassword());
        addToAll_nullPassword(accessTokenId);
        addToAll_incorrectPassword(accessTokenId);
        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        addToAll(accessTokenId, userData.getPassword(), testUser.getUsername());

        removeFromAll_restrictedRole(accessTokenId, userData.getPassword());
        removeFromAll_nullPassword(accessTokenId);
        removeFromAll_incorrectPassword(accessTokenId);
        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        removeFromAll(accessTokenId, userData.getPassword(), testUser.getUsername());
    }

    private void removeFromAll(UUID accessTokenId, String password, String testUserName) {
        int serverPort = getServerPort();
        RoleManagementActions.removeFromAll(serverPort, accessTokenId, password, Constants.ROLE_TEST);

        AwaitilityWrapper.awaitAssert(() -> assertThat(RoleManagementActions.getRoles(serverPort, accessTokenId, testUserName).getFirst().getRoles()).contains(Constants.ROLE_TEST));
    }

    private void removeFromAll_incorrectPassword(UUID accessTokenId) {
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessTokenId, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessTokenId, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessTokenId, INCORRECT_PASSWORD, Constants.ROLE_TEST), 401, ErrorCode.ACCOUNT_LOCKED);
    }

    private void removeFromAll_nullPassword(UUID accessTokenId) {
        ResponseValidator.verifyInvalidParam(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessTokenId, null, Constants.ROLE_TEST), "password", "must not be null");
    }

    private void removeFromAll_restrictedRole(UUID accessTokenId, String password) {
        ResponseValidator.verifyForbiddenOperation(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessTokenId, password, Constants.ROLE_ADMIN));
    }

    private void addToAll(UUID accessTokenId, String password, String testUserName) {
        int serverPort = getServerPort();
        RoleManagementActions.addToAll(serverPort, accessTokenId, password, Constants.ROLE_TEST);

        AwaitilityWrapper.awaitAssert(() -> assertThat(RoleManagementActions.getRoles(serverPort, accessTokenId, testUserName).getFirst().getRoles()).contains(Constants.ROLE_TEST));
    }

    private void addToAll_incorrectPassword(UUID accessTokenId) {
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getAddToAllResponse(getServerPort(), accessTokenId, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getAddToAllResponse(getServerPort(), accessTokenId, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getAddToAllResponse(getServerPort(), accessTokenId, INCORRECT_PASSWORD, Constants.ROLE_TEST), 401, ErrorCode.ACCOUNT_LOCKED);
    }

    private void addToAll_nullPassword(UUID accessTokenId) {
        ResponseValidator.verifyInvalidParam(RoleManagementActions.getAddToAllResponse(getServerPort(), accessTokenId, null, Constants.ROLE_TEST), "password", "must not be null");
    }

    private void addToAll_restrictedRole(UUID accessTokenId, String password) {
        ResponseValidator.verifyForbiddenOperation(RoleManagementActions.getAddToAllResponse(getServerPort(), accessTokenId, password, Constants.ROLE_ADMIN));
    }
}
