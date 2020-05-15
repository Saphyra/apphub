package com.github.saphyra.integration.backend.login;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.ModulesPageActions;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.LoginResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
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

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(401);
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

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(401);
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

        ModulesPageActions.logout(accessTokenId);

        Response response = ModulesPageActions.getLogoutResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(401);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Jelentkezz be újra!");
    }
}
