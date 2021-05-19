package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.actions.AccountActions;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.model.LanguageResponse;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeLanguageTest extends TestBase {
    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @DataProvider(name = "biLocaleDataProvider")
    public Object[][] biLocaleDataProvider() {
        return new Object[][]{
            new Object[]{Language.ENGLISH, Language.HUNGARIAN},
            new Object[]{Language.HUNGARIAN, Language.ENGLISH}
        };
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullLanguage(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        Response response = AccountActions.getChangeLanguageResponse(locale, accessTokenId, null);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams().get("value")).isEqualTo("language must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void languageNotSupported(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        Response response = AccountActions.getChangeLanguageResponse(locale, accessTokenId, "asd");

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams().get("value")).isEqualTo("language not supported");
    }

    @Test(dataProvider = "biLocaleDataProvider")
    public void changeLanguage(Language registerLanguage, Language changeLanguage) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(registerLanguage, userData);

        AccountActions.changeLanguage(registerLanguage, accessTokenId, changeLanguage.getLocale());

        List<LanguageResponse> languageResponses = AccountActions.getLanguages(registerLanguage, accessTokenId);

        assertThat(
            languageResponses.stream()
                .filter(LanguageResponse::isActual)
                .map(LanguageResponse::getLanguage)
                .collect(Collectors.toList())
        ).containsExactly(changeLanguage.getLocale());
    }
}
