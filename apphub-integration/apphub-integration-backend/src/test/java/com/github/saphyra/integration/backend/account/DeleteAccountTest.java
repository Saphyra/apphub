package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.actions.AccountActions;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
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

public class DeleteAccountTest extends BackEndTest {
    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullPassword(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        OneParamRequest<String> request = new OneParamRequest<>(null);

        Response response = AccountActions.getDeleteAccountResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams().get("password")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void incorrectPassword(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        OneParamRequest<String> request = new OneParamRequest<>(DataConstants.INVALID_PASSWORD);

        Response response = AccountActions.getDeleteAccountResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.BAD_PASSWORD));
    }

    @Test(dataProvider = "localeDataProvider")
    public void successfulDeletion(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        OneParamRequest<String> request = new OneParamRequest<>(DataConstants.VALID_PASSWORD);

        Response response = AccountActions.getDeleteAccountResponse(locale, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        Response loginResponse = IndexPageActions.getLoginResponse(locale, userData.toLoginRequest());

        assertThat(loginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = loginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.BAD_CREDENTIALS));
    }
}
