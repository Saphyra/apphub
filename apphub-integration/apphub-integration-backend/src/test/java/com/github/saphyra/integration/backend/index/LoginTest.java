package com.github.saphyra.integration.backend.index;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.ModulesActions;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.LoginResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void loginAndOut(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();

        //Unknown email
        LoginRequest unknownEmailRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();
        Response unknownEmailResponse = IndexPageActions.getLoginResponse(language, unknownEmailRequest);
        verifyBadCredentials(language, unknownEmailResponse);

        //Incorrect password
        IndexPageActions.registerUser(language, userData.toRegistrationRequest());

        LoginRequest incorrectPasswordRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password("asd")
            .build();
        Response incorrectPasswordResponse = IndexPageActions.getLoginResponse(language, incorrectPasswordRequest);
        verifyBadCredentials(language, incorrectPasswordResponse);

        //Successful login - remember me
        LoginRequest rememberMeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(true)
            .build();

        LoginResponse rememberMeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(language, rememberMeLoginRequest);
        assertThat(rememberMeLoginResponse.getExpirationDays()).isEqualTo(365);

        LocalDateTime newLastAccess = LocalDateTime.now().minusDays(100);
        DatabaseUtil.updateAccessTokenLastAccess(rememberMeLoginResponse.getAccessTokenId(), newLastAccess);

        Response modulesResponse = ModulesActions.getModulesResponse(language, rememberMeLoginResponse.getAccessTokenId());
        assertThat(modulesResponse.getStatusCode()).isEqualTo(200);

        //One time login
        LoginRequest oneTimeLoginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        LoginResponse oneTimeLoginResponse = IndexPageActions.getSuccessfulLoginResponse(language, oneTimeLoginRequest);
        assertThat(oneTimeLoginResponse.getExpirationDays()).isNull();

        //Logout
        UUID accessTokenId = oneTimeLoginResponse.getAccessTokenId();
        ModulesActions.logout(language, accessTokenId);

        Response response = ModulesActions.getLogoutResponse(language, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.SESSION_EXPIRED));
    }

    private void verifyBadCredentials(Language locale, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.BAD_CREDENTIALS));
    }
}
