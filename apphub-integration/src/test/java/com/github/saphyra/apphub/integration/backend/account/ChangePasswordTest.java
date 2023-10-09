package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.DataConstants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "account"})
    public void changePassword(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        nullNewPassword(language, userData, accessTokenId);
        tooShortPassword(language, userData, accessTokenId);
        tooLongPassword(language, userData, accessTokenId);
        nullPassword(language, accessTokenId);
        incorrectPassword(language, accessTokenId);
        successfulPasswordChange(language, userData, accessTokenId);
    }

    private static void nullNewPassword(Language language, RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest nullNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(null)
            .password(userData.getPassword())
            .build();
        Response nullNewPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, nullNewPasswordRequest);
        verifyInvalidParam(language, nullNewPasswordResponse, "newPassword", "must not be null");
    }

    private static void tooShortPassword(Language language, RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest tooShortNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_SHORT_PASSWORD)
            .password(userData.getPassword())
            .build();
        Response tooShortNewPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, tooShortNewPasswordRequest);
        verifyInvalidParam(language, tooShortNewPasswordResponse, "password", "too short");
    }

    private static void tooLongPassword(Language language, RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest tooLongNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_LONG_PASSWORD)
            .password(userData.getPassword())
            .build();
        Response tooLongNewPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, tooLongNewPasswordRequest);
        verifyInvalidParam(language, tooLongNewPasswordResponse, "password", "too long");
    }

    private static void nullPassword(Language language, UUID accessTokenId) {
        ChangePasswordRequest nullPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, nullPasswordRequest);
        verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(Language language, UUID accessTokenId) {
        ChangePasswordRequest incorrectPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, incorrectPasswordRequest);
        verifyBadRequest(language, incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void successfulPasswordChange(Language language, RegistrationParameters userData, UUID accessTokenId) {
        ChangePasswordRequest successfulPasswordChangeRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.VALID_PASSWORD)
            .build();
        Response successfulPasswordChangeResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, successfulPasswordChangeRequest);

        assertThat(successfulPasswordChangeResponse.getStatusCode()).isEqualTo(200);

        Response failedLoginResponse = IndexPageActions.getLoginResponse(language, LoginRequest.builder().password(userData.getPassword()).email(userData.getEmail()).build());
        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = failedLoginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.BAD_CREDENTIALS));

        IndexPageActions.login(language, LoginRequest.builder().password(DataConstants.VALID_PASSWORD2).email(userData.getEmail()).build());
    }
}