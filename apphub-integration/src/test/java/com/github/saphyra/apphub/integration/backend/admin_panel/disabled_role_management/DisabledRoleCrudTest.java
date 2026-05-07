package com.github.saphyra.apphub.integration.backend.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.DisabledRoleActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.feature_lock.Feature;
import com.github.saphyra.apphub.integration.core.feature_lock.FeatureLocked;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.DisabledRoleResponse;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class DisabledRoleCrudTest extends BackEndTest {
    @Test(groups = {"be", "admin-panel"})
    @FeatureLocked(Feature.ROLE_TEST)
    public void disabledRoleCrud() {
        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        //Initial check
        assertThat(DisabledRoleActions.getDisabledRoles(getServerPort(), accessToken)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));

        disableRole_unknownRole(testUser, accessToken);
        accessToken = disableRole_incorrectPassword(userData, accessToken);
        disableRole(testUser, accessToken);
        accessToken = enableRole_incorrectPassword(userData, accessToken);
        enableRole(testUser, accessToken);
    }

    private static void disableRole_unknownRole(RegistrationParameters testUser, String accessToken) {
        Response unknownRoleResponse = DisabledRoleActions.getDisableRoleResponse(getServerPort(), accessToken, testUser.getPassword(), "asd");
        verifyInvalidParam(unknownRoleResponse, "role", "unknown or cannot be disabled");
    }

    private static String disableRole_incorrectPassword(RegistrationParameters userData, String accessToken) {
        String ati = accessToken;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(_ -> {
                Response incorrectPasswordDisableResponse = DisabledRoleActions.getDisableRoleResponse(getServerPort(), ati, "asd", Constants.ROLE_TEST);
                verifyBadRequest(incorrectPasswordDisableResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedDisableRoleResponse = DisabledRoleActions.getDisableRoleResponse(getServerPort(), accessToken, "asd", Constants.ROLE_TEST);
        verifyErrorResponse(accountLockedDisableRoleResponse, 423, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        return accessToken;
    }

    private static void disableRole(RegistrationParameters testUser, String accessToken) {
        Response disableRoleResponse = DisabledRoleActions.getDisableRoleResponse(getServerPort(), accessToken, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(disableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(getServerPort(), accessToken)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, true));
    }

    private static String enableRole_incorrectPassword(RegistrationParameters userData, String accessToken) {
        String ati2 = accessToken;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(_ -> {
                Response incorrectPasswordEnableResponse = DisabledRoleActions.getEnableRoleResponse(getServerPort(), ati2, "asd", Constants.ROLE_TEST);
                verifyBadRequest(incorrectPasswordEnableResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response accountLockedEnableResponse = DisabledRoleActions.getEnableRoleResponse(getServerPort(), ati2, "asd", Constants.ROLE_TEST);
        verifyErrorResponse(accountLockedEnableResponse, 423, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        return accessToken;
    }

    private static void enableRole(RegistrationParameters testUser, String accessToken) {
        Response enableRoleResponse = DisabledRoleActions.getEnableRoleResponse(getServerPort(), accessToken, testUser.getPassword(), Constants.ROLE_TEST);
        assertThat(enableRoleResponse.getStatusCode()).isEqualTo(200);
        assertThat(DisabledRoleActions.getDisabledRoles(getServerPort(), accessToken)).contains(new DisabledRoleResponse(Constants.ROLE_TEST, false));
    }
}
