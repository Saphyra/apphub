package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.feature_lock.Feature;
import com.github.saphyra.apphub.integration.core.feature_lock.FeatureLocked;
import com.github.saphyra.apphub.integration.core.testng.PermitCount;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import static com.github.saphyra.apphub.integration.framework.DataConstants.INCORRECT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

public class RolesForAllTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.ROLE_TEST)
    @PermitCount(value = PermitCount.PermitCountType.ALL)
    public void rolesForAll() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DynamoDbUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());

        addToAll_restrictedRole(accessToken, userData.getPassword());
        addToAll_nullPassword(accessToken);
        addToAll_incorrectPassword(accessToken);
        DynamoDbUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        addToAll(accessToken, userData.getPassword(), testUser.getUsername());

        removeFromAll_restrictedRole(accessToken, userData.getPassword());
        removeFromAll_nullPassword(accessToken);
        removeFromAll_incorrectPassword(accessToken);
        DynamoDbUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        removeFromAll(accessToken, userData.getPassword(), testUser.getUsername());
    }

    private void removeFromAll(String accessToken, String password, String testUserName) {
        int serverPort = getServerPort();
        RoleManagementActions.removeFromAll(serverPort, accessToken, password, Constants.ROLE_TEST);

        AwaitilityWrapper.awaitAssert(() -> assertThat(RoleManagementActions.getRoles(serverPort, accessToken, testUserName).getFirst().getRoles()).contains(Constants.ROLE_TEST));
    }

    private void removeFromAll_incorrectPassword(String accessToken) {
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessToken, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessToken, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessToken, INCORRECT_PASSWORD, Constants.ROLE_TEST), 423, ErrorCode.ACCOUNT_LOCKED);
    }

    private void removeFromAll_nullPassword(String accessToken) {
        ResponseValidator.verifyInvalidParam(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessToken, null, Constants.ROLE_TEST), "password", "must not be null");
    }

    private void removeFromAll_restrictedRole(String accessToken, String password) {
        ResponseValidator.verifyForbiddenOperation(RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessToken, password, Constants.ROLE_ADMIN));
    }

    private void addToAll(String accessToken, String password, String testUserName) {
        int serverPort = getServerPort();
        RoleManagementActions.addToAll(serverPort, accessToken, password, Constants.ROLE_TEST);

        AwaitilityWrapper.awaitAssert(() -> assertThat(RoleManagementActions.getRoles(serverPort, accessToken, testUserName).getFirst().getRoles()).contains(Constants.ROLE_TEST));
    }

    private void addToAll_incorrectPassword(String accessToken) {
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getAddToAllResponse(getServerPort(), accessToken, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getAddToAllResponse(getServerPort(), accessToken, INCORRECT_PASSWORD, Constants.ROLE_TEST), 400, ErrorCode.INCORRECT_PASSWORD);
        ResponseValidator.verifyErrorResponse(RoleManagementActions.getAddToAllResponse(getServerPort(), accessToken, INCORRECT_PASSWORD, Constants.ROLE_TEST), 423, ErrorCode.ACCOUNT_LOCKED);
    }

    private void addToAll_nullPassword(String accessToken) {
        ResponseValidator.verifyInvalidParam(RoleManagementActions.getAddToAllResponse(getServerPort(), accessToken, null, Constants.ROLE_TEST), "password", "must not be null");
    }

    private void addToAll_restrictedRole(String accessToken, String password) {
        ResponseValidator.verifyForbiddenOperation(RoleManagementActions.getAddToAllResponse(getServerPort(), accessToken, password, Constants.ROLE_ADMIN));
    }
}
