package com.github.saphyra.apphub.test.backend.login;

import com.github.saphyra.aphub.lib.model.ErrorResponse;
import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.test.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.test.backend.actions.MainMenuPageActions;
import com.github.saphyra.apphub.test.common.integration.TestBase;
import com.github.saphyra.apphub.test.common.integration.model.RegistrationParameters;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends TestBase {
    @Test
    public void emailDoesNotExist() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();

        Response response = IndexPageActions.getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Az email cím és jelszó kombinációja ismeretlen.");
    }

    @Test
    public void wrongPassword() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password("asd")
            .build();

        Response response = IndexPageActions.getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Az email cím és jelszó kombinációja ismeretlen.");
    }

    @Test
    public void successfulLogin_rememberMe() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(true)
            .build();

        LoginResponse response = IndexPageActions.getSuccessfulLoginResponse(loginRequest);
        assertThat(response.getExpirationDays()).isEqualTo(365);
    }

    @Test
    public void successfulLogin_oneTimeLogin() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        LoginResponse response = IndexPageActions.getSuccessfulLoginResponse(loginRequest);
        assertThat(response.getExpirationDays()).isNull();
    }

    @Test
    public void successfulLogout() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();
        UUID accessTokenId = IndexPageActions.login(loginRequest);

        MainMenuPageActions.logout(accessTokenId);

        Response response = MainMenuPageActions.getLogoutResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Jelentkezz be újra!");
    }
}
