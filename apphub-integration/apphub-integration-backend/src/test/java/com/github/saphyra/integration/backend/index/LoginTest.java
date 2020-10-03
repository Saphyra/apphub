package com.github.saphyra.integration.backend.index;

import com.github.saphyra.apphub.integration.backend.actions.ModulesPageActions;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.*;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.LoginResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void emailDoesNotExist(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();

        Response response = IndexPageActions.getLoginResponse(locale, loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_BAD_CREDENTIALS));
    }

    @Test(dataProvider = "localeDataProvider")
    public void wrongPassword(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(locale, userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password("asd")
            .build();

        Response response = IndexPageActions.getLoginResponse(locale, loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_BAD_CREDENTIALS));
    }

    @Test
    public void successfulLogin_rememberMe() throws InterruptedException {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(true)
            .build();

        LoginResponse response = IndexPageActions.getSuccessfulLoginResponse(language, loginRequest);
        assertThat(response.getExpirationDays()).isEqualTo(365);

        OffsetDateTime newLastAccess = OffsetDateTime.now().minusDays(100);
        DatabaseUtil.updateAccessTokenLastAccess(response.getAccessTokenId(), newLastAccess);

        Response modulesResponse = ModulesPageActions.getModulesResponse(language, response.getAccessTokenId());
        assertThat(modulesResponse.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void successfulLogin_oneTimeLogin() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        LoginResponse response = IndexPageActions.getSuccessfulLoginResponse(language, loginRequest);
        assertThat(response.getExpirationDays()).isNull();
    }

    @Test
    public void callEndpointWithAuthHeader() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();

        LoginResponse loginResponse = IndexPageActions.getSuccessfulLoginResponse(language, loginRequest);

        Response response = RequestFactory.createRequest(language)
            .header(Constants.AUTHORIZATION_HEADER, loginResponse.getAccessTokenId())
            .get(UrlFactory.create(Endpoints.GET_MODULES_OF_USER));


        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test(dataProvider = "localeDataProvider")
    public void successfulLogout(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(locale, userData.toRegistrationRequest());

        LoginRequest loginRequest = LoginRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .rememberMe(false)
            .build();
        UUID accessTokenId = IndexPageActions.login(locale, loginRequest);

        ModulesPageActions.logout(locale, accessTokenId);

        Response response = ModulesPageActions.getLogoutResponse(locale, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(401);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_SESSION_EXPIRED));
    }
}
