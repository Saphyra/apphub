package com.github.saphyra.apphub.integration.backend.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.DisabledRoleActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleCrud extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    public void disabledRoleCrud() {
        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(testUser.toRegistrationRequest());

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        //Initial check
        assertThat(DisabledRoleActions.getDisabledRoles(accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));

        disableRole_unknownRole(testUser, accessTokenId);
        accessTokenId = disableRole_incorrectPassword(userData, accessTokenId);
        disableRole(testUser, accessTokenId);
        accessTokenId = enableRole_incorrectPassword(userData, accessTokenId);
        enableRole(testUser, accessTokenId);
    }

    private static void disableRole_unknownRole(RegistrationParameters testUser, UUID accessTokenId) {
        Response unknownRoleResponse = DisabledRoleActions.getDisableRoleResponse(accessTokenId, testUser.getPassword(), "asd");
        verifyInvalidParam(unknownRoleResponse, "role", "unknown or cannot be disabled");
    }

    private static UUID disableRole_incorrectPassword(RegistrationParameters userData, UUID accessTokenId) {
        UUID ati = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response incorrectPasswordDisableResponse = DisabledRoleActions.getDisableRoleResponse(ati, "asd", Constants.ROLE_TEST);
                verifyBadRequest(incorrectPasswordDisableResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedDisableRoleResponse = DisabledRoleActions.getDisableRoleResponse(accessTokenId, "asd", Constants.ROLE_TEST);
        verifyErrorResponse(accountLockedDisableRoleResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(userData.toLoginRequest());
        return accessTokenId;
    }

    private static void disableRole(RegistrationParameters testUser, UUID accessTokenId) {
        Response disableRoleResponse = DisabledRoleActions.getDisableRoleResponse(accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(disableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, true));
    }

    private static UUID enableRole_incorrectPassword(RegistrationParameters userData, UUID accessTokenId) {
        UUID ati2 = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response incorrectPasswordEnableResponse = DisabledRoleActions.getEnableRoleResponse(ati2, "asd", Constants.ROLE_TEST);
                verifyBadRequest(incorrectPasswordEnableResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedEnableResponse = DisabledRoleActions.getEnableRoleResponse(ati2, "asd", Constants.ROLE_TEST);
        verifyErrorResponse(accountLockedEnableResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(userData.toLoginRequest());
        return accessTokenId;
    }

    private static void enableRole(RegistrationParameters testUser, UUID accessTokenId) {
        Response enableRoleResponse = DisabledRoleActions.getEnableRoleResponse(accessTokenId, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(enableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(accessTokenId)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));
    }
}
