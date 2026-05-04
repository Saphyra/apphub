package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.user.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void changePassword() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        nullNewPassword(userData, accessToken);
        tooShortPassword(userData, accessToken);
        tooLongPassword(userData, accessToken);
        nullPassword(accessToken);
        nullDeactivateAllSessions(accessToken, userData);
        incorrectPassword(accessToken); //TODO test lockout
        successfulPasswordChange(userData, accessToken);
    }

    private static void nullNewPassword(RegistrationParameters userData, String accessToken) {
        ChangePasswordRequest nullNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(null)
            .password(userData.getPassword())
            .deactivateAllSessions(false)
            .build();
        Response nullNewPasswordResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, nullNewPasswordRequest);
        verifyInvalidParam(nullNewPasswordResponse, "newPassword", "must not be null");
    }

    private static void tooShortPassword(RegistrationParameters userData, String accessToken) {
        ChangePasswordRequest tooShortNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_SHORT_PASSWORD)
            .password(userData.getPassword())
            .deactivateAllSessions(false)
            .build();
        Response tooShortNewPasswordResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, tooShortNewPasswordRequest);
        verifyInvalidParam(tooShortNewPasswordResponse, "password", "too short");
    }

    private static void tooLongPassword(RegistrationParameters userData, String accessToken) {
        ChangePasswordRequest tooLongNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_LONG_PASSWORD)
            .password(userData.getPassword())
            .deactivateAllSessions(false)
            .build();
        Response tooLongNewPasswordResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, tooLongNewPasswordRequest);
        verifyInvalidParam(tooLongNewPasswordResponse, "password", "too long");
    }

    private static void nullPassword(String accessToken) {
        ChangePasswordRequest nullPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(null)
            .deactivateAllSessions(false)
            .build();
        Response nullPasswordResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, nullPasswordRequest);
        verifyInvalidParam(nullPasswordResponse, "password", "must not be null or blank");
    }

    private static void nullDeactivateAllSessions(String accessToken, RegistrationParameters userData) {
        ChangePasswordRequest nullPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(userData.getPassword())
            .deactivateAllSessions(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, nullPasswordRequest);
        verifyInvalidParam(nullPasswordResponse, "deactivateAllSessions", "must not be null");
    }

    private static void incorrectPassword(String accessToken) {
        ChangePasswordRequest incorrectPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.INCORRECT_PASSWORD)
            .deactivateAllSessions(false)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, incorrectPasswordRequest);
        verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void successfulPasswordChange(RegistrationParameters userData, String accessToken) {
        ChangePasswordRequest successfulPasswordChangeRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.VALID_PASSWORD)
            .deactivateAllSessions(false)
            .build();
        Response successfulPasswordChangeResponse = AccountActions.getChangePasswordResponse(getServerPort(), accessToken, successfulPasswordChangeRequest);

        assertThat(successfulPasswordChangeResponse.getStatusCode()).isEqualTo(200);

        Response failedLoginResponse = IndexPageActions.getLoginResponse(getServerPort(), LoginRequest.builder().password(userData.getPassword()).userIdentifier(userData.getEmail()).build());
        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = failedLoginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());

        IndexPageActions.login(getServerPort(), LoginRequest.builder().password(DataConstants.VALID_PASSWORD2).userIdentifier(userData.getEmail()).build());
    }

    @Test(groups = {"be", "account"})
    public void changePassword_deactivateAllSessions() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData);
        String accessToken2 = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();

        ChangePasswordRequest successfulPasswordChangeRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.VALID_PASSWORD)
            .deactivateAllSessions(true)
            .build();
        AccountActions.changePassword(getServerPort(), accessToken1, successfulPasswordChangeRequest);

        assertThat(ModulesActions.getModulesResponse(getServerPort(), accessToken1).getStatusCode()).isEqualTo(401);
        assertThat(ModulesActions.getModulesResponse(getServerPort(), accessToken2).getStatusCode()).isEqualTo(401);
    }
}
