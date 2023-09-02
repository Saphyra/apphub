package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeEmailTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "account"})
    public void changeEmail(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);

        nullEmail(language, userData1, accessTokenId1);
        invalidEmail(language, userData1, accessTokenId1);
        nullPassword(language, accessTokenId1);
        incorrectPassword(language, accessTokenId1);
        emailAlreadyExists(language, userData1, accessTokenId1);
        successfulChange(language, userData1, accessTokenId1);
    }

    private static void nullEmail(Language language, RegistrationParameters userData1, UUID accessTokenId1) {
        ChangeEmailRequest nullEmailRequest = ChangeEmailRequest.builder()
            .email(null)
            .password(userData1.getPassword())
            .build();
        Response nullEmailResponse = AccountActions.getChangeEmailResponse(language, accessTokenId1, nullEmailRequest);
        ResponseValidator.verifyInvalidParam(language, nullEmailResponse, "email", "must not be null");
    }

    private static void invalidEmail(Language language, RegistrationParameters userData1, UUID accessTokenId1) {
        ChangeEmailRequest invalidEmailRequest = ChangeEmailRequest.builder()
            .email("a@a.a")
            .password(userData1.getPassword())
            .build();
        Response invalidEmailResponse = AccountActions.getChangeEmailResponse(language, accessTokenId1, invalidEmailRequest);
        ResponseValidator.verifyInvalidParam(language, invalidEmailResponse, "email", "invalid format");
    }

    private static void nullPassword(Language language, UUID accessTokenId1) {
        ChangeEmailRequest nullPasswordRequest = ChangeEmailRequest.builder()
            .email(RandomDataProvider.generateEmail())
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangeEmailResponse(language, accessTokenId1, nullPasswordRequest);
        ResponseValidator.verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(Language language, UUID accessTokenId1) {
        ChangeEmailRequest incorrectPasswordRequest = ChangeEmailRequest.builder()
            .email(RandomDataProvider.generateEmail())
            .password("incorrect-password")
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangeEmailResponse(language, accessTokenId1, incorrectPasswordRequest);
        ResponseValidator.verifyBadRequest(language, incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void emailAlreadyExists(Language language, RegistrationParameters userData1, UUID accessTokenId1) {
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(language, userData2);
        ChangeEmailRequest emailAlreadyExistsRequest = ChangeEmailRequest.builder()
            .email(userData2.getEmail())
            .password(userData1.getPassword())
            .build();
        Response emailAlreadyExistsResponse = AccountActions.getChangeEmailResponse(language, accessTokenId1, emailAlreadyExistsRequest);
        assertThat(emailAlreadyExistsResponse.getStatusCode()).isEqualTo(409);
        ErrorResponse emailAlreadyExistsErrorResponse = emailAlreadyExistsResponse.getBody().as(ErrorResponse.class);
        assertThat(emailAlreadyExistsErrorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
        assertThat(emailAlreadyExistsErrorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.EMAIL_ALREADY_EXISTS));
    }

    private static void successfulChange(Language language, RegistrationParameters userData1, UUID accessTokenId1) {
        String newEmail = RandomDataProvider.generateEmail();
        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(newEmail)
            .password(userData1.getPassword())
            .build();
        Response response = AccountActions.getChangeEmailResponse(language, accessTokenId1, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
        Response failedLoginResponse = IndexPageActions.getLoginResponse(language, LoginRequest.builder().password(userData1.getPassword()).email(userData1.getEmail()).build());
        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = failedLoginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.BAD_CREDENTIALS));
        IndexPageActions.login(language, LoginRequest.builder().password(userData1.getPassword()).email(newEmail).build());
    }
}
