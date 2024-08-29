package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeEmailTest extends BackEndTest {
    @Test(groups = {"be", "account"})
    public void changeEmail() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        nullEmail(userData1, accessTokenId1);
        invalidEmail(userData1, accessTokenId1);
        nullPassword(accessTokenId1);
        incorrectPassword(accessTokenId1);
        emailAlreadyExists(userData1, accessTokenId1);
        successfulChange(userData1, accessTokenId1);
    }

    private static void nullEmail(RegistrationParameters userData1, UUID accessTokenId1) {
        ChangeEmailRequest nullEmailRequest = ChangeEmailRequest.builder()
            .email(null)
            .password(userData1.getPassword())
            .build();
        Response nullEmailResponse = AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId1, nullEmailRequest);
        ResponseValidator.verifyInvalidParam(nullEmailResponse, "email", "must not be null");
    }

    private static void invalidEmail(RegistrationParameters userData1, UUID accessTokenId1) {
        ChangeEmailRequest invalidEmailRequest = ChangeEmailRequest.builder()
            .email("a@a.a")
            .password(userData1.getPassword())
            .build();
        Response invalidEmailResponse = AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId1, invalidEmailRequest);
        ResponseValidator.verifyInvalidParam(invalidEmailResponse, "email", "invalid format");
    }

    private static void nullPassword(UUID accessTokenId1) {
        ChangeEmailRequest nullPasswordRequest = ChangeEmailRequest.builder()
            .email(RandomDataProvider.generateEmail())
            .password(null)
            .build();
        Response nullPasswordResponse = AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId1, nullPasswordRequest);
        ResponseValidator.verifyInvalidParam(nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(UUID accessTokenId1) {
        ChangeEmailRequest incorrectPasswordRequest = ChangeEmailRequest.builder()
            .email(RandomDataProvider.generateEmail())
            .password("incorrect-password")
            .build();
        Response incorrectPasswordResponse = AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId1, incorrectPasswordRequest);
        ResponseValidator.verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void emailAlreadyExists(RegistrationParameters userData1, UUID accessTokenId1) {
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), userData2);
        ChangeEmailRequest emailAlreadyExistsRequest = ChangeEmailRequest.builder()
            .email(userData2.getEmail())
            .password(userData1.getPassword())
            .build();
        Response emailAlreadyExistsResponse = AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId1, emailAlreadyExistsRequest);
        assertThat(emailAlreadyExistsResponse.getStatusCode()).isEqualTo(409);
        ErrorResponse emailAlreadyExistsErrorResponse = emailAlreadyExistsResponse.getBody().as(ErrorResponse.class);
        assertThat(emailAlreadyExistsErrorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
    }

    private static void successfulChange(RegistrationParameters userData1, UUID accessTokenId1) {
        String newEmail = RandomDataProvider.generateEmail();
        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(newEmail)
            .password(userData1.getPassword())
            .build();
        Response response = AccountActions.getChangeEmailResponse(getServerPort(), accessTokenId1, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
        Response failedLoginResponse = IndexPageActions.getLoginResponse(getServerPort(), LoginRequest.builder().password(userData1.getPassword()).userIdentifier(userData1.getEmail()).build());
        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = failedLoginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        IndexPageActions.login(getServerPort(), LoginRequest.builder().password(userData1.getPassword()).userIdentifier(newEmail).build());
    }
}
