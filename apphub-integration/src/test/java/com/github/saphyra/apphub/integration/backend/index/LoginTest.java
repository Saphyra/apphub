package com.github.saphyra.apphub.integration.backend.index;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class LoginTest extends BackEndTest {
    @Test(groups = {"be", "index"})
    public void loginAndOut() {
        RegistrationParameters userData = RegistrationParameters.validParameters();

        unknownEmail(userData);
        LoginRequest incorrectPasswordRequest = incorrectPassword(userData);
        successfulLogin_rememberMe(userData);

        TokenResponse oneTimeLoginResponse = oneTimeLogin(userData);
        logout(oneTimeLoginResponse);
        lockUser(userData, incorrectPasswordRequest);
    }

    private static void unknownEmail(RegistrationParameters userData) {
        LoginRequest unknownEmailRequest = LoginRequest.builder()
            .userIdentifier(userData.getEmail())
            .password(userData.getPassword())
            .build();
        Response unknownEmailResponse = IndexPageActions.getLoginResponse(getServerPort(), unknownEmailRequest);
        verifyErrorResponse(unknownEmailResponse, 401, ErrorCode.BAD_CREDENTIALS);
    }

    private static LoginRequest incorrectPassword(RegistrationParameters userData) {
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());

        LoginRequest incorrectPasswordRequest = LoginRequest.builder()
            .userIdentifier(userData.getEmail())
            .password("asd")
            .build();
        Response incorrectPasswordResponse = IndexPageActions.getLoginResponse(getServerPort(), incorrectPasswordRequest);
        verifyErrorResponse(incorrectPasswordResponse, 401, ErrorCode.BAD_CREDENTIALS);
        return incorrectPasswordRequest;
    }

    private static void successfulLogin_rememberMe(RegistrationParameters userData) {
        LoginRequest rememberMeLoginRequest = LoginRequest.builder()
            .userIdentifier(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(true)
            .build();

        TokenResponse rememberMeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(getServerPort(), rememberMeLoginRequest);
        assertThat(rememberMeLoginResponse.getRefreshToken().getExpiration()).isGreaterThan(LocalDateTime.now().plusDays(29).toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private static TokenResponse oneTimeLogin(RegistrationParameters userData) {
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .userIdentifier(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        TokenResponse oneTimeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(getServerPort(), oneTimeLoginRequest);
        assertThat(oneTimeLoginResponse.getRefreshToken().getExpiration()).isLessThan(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli());
        return oneTimeLoginResponse;
    }

    private static void logout(TokenResponse tokenResponse) {
        ModulesActions.logout(getServerPort(), tokenResponse.getAccessToken().getJwt(), tokenResponse.getRefreshToken().getJwt());

        Response response = ModulesActions.getModulesResponse(getServerPort(), tokenResponse.getAccessToken().getJwt());

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
    }

    private static void lockUser(RegistrationParameters userData, LoginRequest incorrectPasswordRequest) {
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .userIdentifier(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        Stream.generate(() -> "")
            .limit(2)
            .map(_ -> IndexPageActions.getLoginResponse(getServerPort(), incorrectPasswordRequest))
            .forEach(r -> verifyErrorResponse(r, 401, ErrorCode.BAD_CREDENTIALS));

        Response lockedLoginResponse = IndexPageActions.getLoginResponse(getServerPort(), incorrectPasswordRequest);
        verifyErrorResponse(lockedLoginResponse, 423, ErrorCode.ACCOUNT_LOCKED);

        lockedLoginResponse = IndexPageActions.getLoginResponse(getServerPort(), oneTimeLoginRequest);
        verifyErrorResponse(lockedLoginResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());

        IndexPageActions.getSuccessfulLoginResponse(getServerPort(), oneTimeLoginRequest);
    }
}
