package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.actions.AccountPageActions;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.model.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeEmailTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullEmail(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(null)
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeEmailResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("email")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void invalidEmail(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email("a@a.a")
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeEmailResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("email")).isEqualTo("invalid format");
    }

    @Test(dataProvider = "localeDataProvider")
    public void emailAlreadyExists(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(userData.getEmail())
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeEmailResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(409);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_EMAIL_ALREADY_IN_USE));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullPassword(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(RandomDataProvider.generateEmail())
            .password(null)
            .build();

        Response response = AccountPageActions.getChangeEmailResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("password")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void incorrectPassword(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(RandomDataProvider.generateEmail())
            .password("incorrect-password")
            .build();

        Response response = AccountPageActions.getChangeEmailResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_BAD_PASSWORD));
    }

    @Test
    public void successfulChange() {
        Language locale = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        String newEmail = RandomDataProvider.generateEmail();
        ChangeEmailRequest request = ChangeEmailRequest.builder()
            .email(newEmail)
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeEmailResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        Response failedLoginResponse = IndexPageActions.getLoginResponse(locale, LoginRequest.builder().password(userData.getPassword()).email(userData.getEmail()).build());
        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = failedLoginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_BAD_CREDENTIALS));

        IndexPageActions.login(locale, LoginRequest.builder().password(userData.getPassword()).email(newEmail).build());
    }
}
