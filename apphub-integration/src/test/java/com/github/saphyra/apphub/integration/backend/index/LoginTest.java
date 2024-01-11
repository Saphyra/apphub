package com.github.saphyra.apphub.integration.backend.index;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.LoginResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BackEndTest {
    @Test(groups = {"be", "index"})
    public void loginAndOut() {
        RegistrationParameters userData = RegistrationParameters.validParameters();

        unknownEmail(userData);
        LoginRequest incorrectPasswordRequest = incorrectPassword(userData);
        successfulLogin_rememberMe(userData);

        LoginResponse oneTimeLoginResponse = oneTimeLogin(userData);
        logout(oneTimeLoginResponse);
        lockUser(userData, incorrectPasswordRequest);
    }

    private static void unknownEmail(RegistrationParameters userData) {
        LoginRequest unknownEmailRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();
        Response unknownEmailResponse = IndexPageActions.getLoginResponse(unknownEmailRequest);
        verifyErrorResponse(unknownEmailResponse, 401, ErrorCode.BAD_CREDENTIALS);
    }

    private static LoginRequest incorrectPassword(RegistrationParameters userData) {
        IndexPageActions.registerUser(userData.toRegistrationRequest());

        LoginRequest incorrectPasswordRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password("asd")
            .build();
        Response incorrectPasswordResponse = IndexPageActions.getLoginResponse(incorrectPasswordRequest);
        verifyErrorResponse(incorrectPasswordResponse, 401, ErrorCode.BAD_CREDENTIALS);
        return incorrectPasswordRequest;
    }

    private static void successfulLogin_rememberMe(RegistrationParameters userData) {
        LoginRequest rememberMeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(true)
            .build();

        LoginResponse rememberMeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(rememberMeLoginRequest);
        assertThat(rememberMeLoginResponse.getExpirationDays()).isEqualTo(365);

        LocalDateTime newLastAccess = LocalDateTime.now().minusDays(100);
        DatabaseUtil.updateAccessTokenLastAccess(rememberMeLoginResponse.getAccessTokenId(), newLastAccess);

        Response modulesResponse = ModulesActions.getModulesResponse(rememberMeLoginResponse.getAccessTokenId());
        assertThat(modulesResponse.getStatusCode()).isEqualTo(200);
    }

    private static LoginResponse oneTimeLogin(RegistrationParameters userData) {
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        LoginResponse oneTimeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(oneTimeLoginRequest);
        assertThat(oneTimeLoginResponse.getExpirationDays()).isNull();
        return oneTimeLoginResponse;
    }

    private static void logout(LoginResponse oneTimeLoginResponse) {
        UUID accessTokenId = oneTimeLoginResponse.getAccessTokenId();
        ModulesActions.logout(accessTokenId);

        Response response = ModulesActions.getLogoutResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
    }

    private static void lockUser(RegistrationParameters userData, LoginRequest incorrectPasswordRequest) {
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        Stream.generate(() -> "")
            .limit(2)
            .map(s -> IndexPageActions.getLoginResponse(incorrectPasswordRequest))
            .forEach(r -> verifyErrorResponse(r, 401, ErrorCode.BAD_CREDENTIALS));

        Response lockedLoginResponse = IndexPageActions.getLoginResponse(incorrectPasswordRequest);
        verifyErrorResponse(lockedLoginResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        lockedLoginResponse = IndexPageActions.getLoginResponse(oneTimeLoginRequest);
        verifyErrorResponse(lockedLoginResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());

        IndexPageActions.getSuccessfulLoginResponse(oneTimeLoginRequest);
    }
}
