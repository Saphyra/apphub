package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.actions.AccountActions;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.model.LanguageResponse;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
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

public class ChangeLanguageTest extends BackEndTest {
    @DataProvider(name = "bilanguageDataProvider")
    public Object[][] bilanguageDataProvider() {
        return new Object[][]{
            new Object[]{Language.ENGLISH, Language.HUNGARIAN},
            new Object[]{Language.HUNGARIAN, Language.ENGLISH}
        };
    }

    @Test(dataProvider = "bilanguageDataProvider")
    public void changeLanguage(Language registerLanguage, Language changeLanguage) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(registerLanguage, userData);

        //Null language
        Response nullLanguageResponse = AccountActions.getChangeLanguageResponse(registerLanguage, accessTokenId, null);
        verifyBadRequest(registerLanguage, nullLanguageResponse, "language must not be null");

        //Not supported language
        Response response = AccountActions.getChangeLanguageResponse(registerLanguage, accessTokenId, "asd");
        verifyBadRequest(registerLanguage, response, "language not supported");

        //Change language
        AccountActions.changeLanguage(registerLanguage, accessTokenId, changeLanguage.getLocale());

        List<LanguageResponse> languageResponses = AccountActions.getLanguages(registerLanguage, accessTokenId);

        assertThat(
            languageResponses.stream()
                .filter(LanguageResponse::isActual)
                .map(LanguageResponse::getLanguage)
                .collect(Collectors.toList())
        ).containsExactly(changeLanguage.getLocale());
    }

    private void verifyBadRequest(Language locale, Response response, String value) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams().get("value")).isEqualTo(value);
    }
}
