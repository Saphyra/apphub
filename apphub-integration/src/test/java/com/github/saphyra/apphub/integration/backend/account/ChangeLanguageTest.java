package com.github.saphyra.apphub.integration.backend.account;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.LanguageResponse;
import com.github.saphyra.apphub.integration.structure.api.user.LoginResponse;
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
        UUID accessTokenId = registerAndLogin(registerLanguage, userData);

        nullLanguage(accessTokenId);
        unsupportedLanguage(accessTokenId);
        changeLanguage(changeLanguage, accessTokenId);
    }

    private UUID registerAndLogin(Language registerLanguage, RegistrationParameters userData) {
        Response registerResponse = RequestFactory.createRequest(registerLanguage)
            .body(userData.toRegistrationRequest())
            .post(UrlFactory.create(Endpoints.ACCOUNT_REGISTER));
        assertThat(registerResponse.getStatusCode()).isEqualTo(200);

        Response loginResponse = RequestFactory.createRequest()
            .body(userData.toLoginRequest())
            .post(UrlFactory.create(Endpoints.LOGIN));

        assertThat(loginResponse.getStatusCode()).isEqualTo(200);

        return loginResponse.getBody()
            .as(LoginResponse.class)
            .getAccessTokenId();
    }

    private static void nullLanguage(UUID accessTokenId) {
        Response nullLanguageResponse = AccountActions.getChangeLanguageResponse(accessTokenId, null);
        ResponseValidator.verifyInvalidParam(nullLanguageResponse, "value", "must not be null");
    }

    private static void unsupportedLanguage(UUID accessTokenId) {
        Response response = AccountActions.getChangeLanguageResponse(accessTokenId, "asd");
        ResponseValidator.verifyInvalidParam(response, "value", "not supported");
    }

    private static void changeLanguage(Language changeLanguage, UUID accessTokenId) {
        AccountActions.changeLanguage(accessTokenId, changeLanguage.getLocale());

        List<LanguageResponse> languageResponses = AccountActions.getLanguages(accessTokenId);

        assertThat(
            languageResponses.stream()
                .filter(LanguageResponse::isActual)
                .map(LanguageResponse::getLanguage)
                .collect(Collectors.toList())
        ).containsExactly(changeLanguage.getLocale());
    }
}
