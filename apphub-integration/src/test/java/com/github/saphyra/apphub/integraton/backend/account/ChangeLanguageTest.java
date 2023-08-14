package com.github.saphyra.apphub.integraton.backend.account;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.LanguageResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
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

    @Test(dataProvider = "bilanguageDataProvider", groups = {"be", "account"})
    public void changeLanguage(Language registerLanguage, Language changeLanguage) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(registerLanguage, userData);

        //Null language
        Response nullLanguageResponse = AccountActions.getChangeLanguageResponse(registerLanguage, accessTokenId, null);
        ResponseValidator.verifyInvalidParam(registerLanguage, nullLanguageResponse, "value", "must not be null");

        //Not supported language
        Response response = AccountActions.getChangeLanguageResponse(registerLanguage, accessTokenId, "asd");
        ResponseValidator.verifyInvalidParam(registerLanguage, response, "value", "not supported");

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
}
