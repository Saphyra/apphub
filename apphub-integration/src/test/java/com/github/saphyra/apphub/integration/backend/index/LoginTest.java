package com.github.saphyra.apphub.integration.backend.index;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "index"})
    public void loginAndOut(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();

        unknownEmail(language, userData);
        LoginRequest incorrectPasswordRequest = incorrectPassword(language, userData);
        successfulLogin_rememberMe(language, userData);

        LoginResponse oneTimeLoginResponse = oneTimeLogin(language, userData);
        logout(language, oneTimeLoginResponse);
        lockUser(language, userData, incorrectPasswordRequest);
    }

    private static void unknownEmail(Language language, RegistrationParameters userData) {
        LoginRequest unknownEmailRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();
        Response unknownEmailResponse = IndexPageActions.getLoginResponse(language, unknownEmailRequest);
        verifyErrorResponse(language, unknownEmailResponse, 401, ErrorCode.BAD_CREDENTIALS);
    }

    private static LoginRequest incorrectPassword(Language language, RegistrationParameters userData) {
        IndexPageActions.registerUser(language, userData.toRegistrationRequest());

        LoginRequest incorrectPasswordRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password("asd")
            .build();
        Response incorrectPasswordResponse = IndexPageActions.getLoginResponse(language, incorrectPasswordRequest);
        verifyErrorResponse(language, incorrectPasswordResponse, 401, ErrorCode.BAD_CREDENTIALS);
        return incorrectPasswordRequest;
    }

    private static void successfulLogin_rememberMe(Language language, RegistrationParameters userData) {
        LoginRequest rememberMeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(true)
            .build();

        LoginResponse rememberMeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(language, rememberMeLoginRequest);
        assertThat(rememberMeLoginResponse.getExpirationDays()).isEqualTo(365);

        LocalDateTime newLastAccess = LocalDateTime.now().minusDays(100);
        DatabaseUtil.updateAccessTokenLastAccess(rememberMeLoginResponse.getAccessTokenId(), newLastAccess);

        Response modulesResponse = ModulesActions.getModulesResponse(rememberMeLoginResponse.getAccessTokenId());
        assertThat(modulesResponse.getStatusCode()).isEqualTo(200);
    }

    private static LoginResponse oneTimeLogin(Language language, RegistrationParameters userData) {
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        LoginResponse oneTimeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(language, oneTimeLoginRequest);
        assertThat(oneTimeLoginResponse.getExpirationDays()).isNull();
        return oneTimeLoginResponse;
    }

    private static void logout(Language language, LoginResponse oneTimeLoginResponse) {
        UUID accessTokenId = oneTimeLoginResponse.getAccessTokenId();
        ModulesActions.logout(language, accessTokenId);

        Response response = ModulesActions.getLogoutResponse(language, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.SESSION_EXPIRED));
    }

    private static void lockUser(Language language, RegistrationParameters userData, LoginRequest incorrectPasswordRequest) {
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        Stream.generate(() -> "")
            .limit(2)
            .map(s -> IndexPageActions.getLoginResponse(language, incorrectPasswordRequest))
            .forEach(r -> verifyErrorResponse(language, r, 401, ErrorCode.BAD_CREDENTIALS));

        Response lockedLoginResponse = IndexPageActions.getLoginResponse(language, incorrectPasswordRequest);
        verifyErrorResponse(language, lockedLoginResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        lockedLoginResponse = IndexPageActions.getLoginResponse(language, oneTimeLoginRequest);
        verifyErrorResponse(language, lockedLoginResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());

        IndexPageActions.getSuccessfulLoginResponse(language, oneTimeLoginRequest);
    }
}
