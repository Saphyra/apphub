package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.AccountActions;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.model.account.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyBadRequest;
import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void changePassword(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Null new password
        ChangePasswordRequest nullNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(null)
            .password(userData.getPassword())
            .build();
        Response nullNewPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, nullNewPasswordRequest);
        verifyInvalidParam(language, nullNewPasswordResponse, "newPassword", "must not be null");

        //Too short new password
        ChangePasswordRequest tooShortNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_SHORT_PASSWORD)
            .password(userData.getPassword())
            .build();
        Response tooShortNewPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, tooShortNewPasswordRequest);
        verifyInvalidParam(language, tooShortNewPasswordResponse, "password", "too short");

        //Too long new password
        ChangePasswordRequest tooLongNewPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.TOO_LONG_PASSWORD)
            .password(userData.getPassword())
            .build();
        Response tooLongNewPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, tooLongNewPasswordRequest);
        verifyInvalidParam(language, tooLongNewPasswordResponse, "password", "too long");

        //Null password
        ChangePasswordRequest nullPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, nullPasswordRequest);
        verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");

        //Incorrect password
        ChangePasswordRequest incorrectPasswordRequest = ChangePasswordRequest.builder()
            .newPassword(DataConstants.VALID_PASSWORD2)
            .password(DataConstants.INCORRECT_PASSWORD)
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangePasswordResponse(language, accessTokenId, incorrectPasswordRequest);
        verifyBadRequest(language, incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);

        //Successful password change
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
