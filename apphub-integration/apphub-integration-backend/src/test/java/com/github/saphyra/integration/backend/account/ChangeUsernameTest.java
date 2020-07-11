package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.actions.AccountPageActions;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.model.account.ChangeUsernameRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.RandomDataProvider;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeUsernameTest extends TestBase {
    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullUsername(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(null)
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("username")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void tooShortUsername(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_SHORT_USERNAME)
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_USERNAME_TOO_SHORT));
    }

    @Test(dataProvider = "localeDataProvider")
    public void tooLongUsername(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(DataConstants.TOO_LONG_USERNAME)
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_USERNAME_TOO_LONG));
    }

    @Test(dataProvider = "localeDataProvider")
    public void usernameAlreadyExists(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(userData.getUsername())
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(409);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_USERNAME_ALREADY_EXISTS));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullPassword(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(null)
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

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

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(DataConstants.INVALID_PASSWORD)
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.ERROR_CODE_BAD_PASSWORD));
    }

    @Test(dataProvider = "localeDataProvider")
    public void successfulChange(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        ChangeUsernameRequest request = ChangeUsernameRequest.builder()
            .username(RandomDataProvider.generateUsername())
            .password(userData.getPassword())
            .build();

        Response response = AccountPageActions.getChangeUsernameResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
