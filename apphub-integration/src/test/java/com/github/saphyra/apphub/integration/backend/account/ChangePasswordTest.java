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

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void changePassword() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        nullNewPassword(userData, accessTokenId);
        tooShortPassword(userData, accessTokenId);
        tooLongPassword(userData, accessTokenId);
        nullPassword(accessTokenId);
        nullDeactivateAllSessions(accessTokenId, userData);
        incorrectPassword(accessTokenId);
        successfulPasswordChange(userData, accessTokenId);
    }

    private static void nullNewPassword(RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest nullNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(null)
            .password(userData.getPassword())
            .deactivateAllSessions(false)
            .build();
        Response nullNewPasswordResponse = AccountActions.getChangePasswordResponse(accessTokenId, nullNewPasswordRequest);
        verifyInvalidParam(nullNewPasswordResponse, "newPassword", "must not be null");
    }

    private static void tooShortPassword(RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest tooShortNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_SHORT_PASSWORD)
            .password(userData.getPassword())
            .deactivateAllSessions(false)
            .build();
        Response tooShortNewPasswordResponse = AccountActions.getChangePasswordResponse(accessTokenId, tooShortNewPasswordRequest);
        verifyInvalidParam(tooShortNewPasswordResponse, "password", "too short");
    }

    private static void tooLongPassword(RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest tooLongNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_LONG_PASSWORD)
            .password(userData.getPassword())
            .deactivateAllSessions(false)
            .build();
        Response tooLongNewPasswordResponse = AccountActions.getChangePasswordResponse(accessTokenId, tooLongNewPasswordRequest);
        verifyInvalidParam(tooLongNewPasswordResponse, "password", "too long");
    }

    private static void nullPassword(UUID accessTokenId) {
        ChangePasswordRequest nullPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(null)
            .deactivateAllSessions(false)
            .build();
        Response nullPasswordResponse = AccountActions.getChangePasswordResponse(accessTokenId, nullPasswordRequest);
        verifyInvalidParam(nullPasswordResponse, "password", "must not be null or blank");
    }

    private static void nullDeactivateAllSessions(UUID accessTokenId, RegistrationParameters userData) {
        ChangePasswordRequest nullPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(userData.getPassword())
            .deactivateAllSessions(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangePasswordResponse(accessTokenId, nullPasswordRequest);
        verifyInvalidParam(nullPasswordResponse, "deactivateAllSessions", "must not be null");
    }

    private static void incorrectPassword(UUID accessTokenId) {
        ChangePasswordRequest incorrectPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.INCORRECT_PASSWORD)
            .deactivateAllSessions(false)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangePasswordResponse(accessTokenId, incorrectPasswordRequest);
        verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void successfulPasswordChange(RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest successfulPasswordChangeRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.VALID_PASSWORD)
            .deactivateAllSessions(false)
            .build();
        Response successfulPasswordChangeResponse = AccountActions.getChangePasswordResponse(accessTokenId, successfulPasswordChangeRequest);

        assertThat(successfulPasswordChangeResponse.getStatusCode()).isEqualTo(200);

        Response failedLoginResponse = IndexPageActions.getLoginResponse(LoginRequest.builder().password(userData.getPassword()).email(userData.getEmail()).build());
        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = failedLoginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());

        IndexPageActions.login(LoginRequest.builder().password(DataConstants.VALID_PASSWORD2).email(userData.getEmail()).build());
    }

    @Test(groups = {"be", "account"})
    public void changePassword_deactivateAllSessions() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(userData);
        UUID accessTokenId2 = IndexPageActions.login(userData.toLoginRequest());

        ChangePasswordRequest successfulPasswordChangeRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.VALID_PASSWORD)
            .deactivateAllSessions(true)
            .build();
        AccountActions.changePassword(accessTokenId1, successfulPasswordChangeRequest);

        assertThat(ModulesActions.getModulesResponse(accessTokenId1).getStatusCode()).isEqualTo(401);
        assertThat(ModulesActions.getModulesResponse(accessTokenId2).getStatusCode()).isEqualTo(401);
    }
}
