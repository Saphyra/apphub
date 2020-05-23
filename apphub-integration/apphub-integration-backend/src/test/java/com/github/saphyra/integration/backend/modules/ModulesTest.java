package com.github.saphyra.integration.backend.modules;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.ModulesPageActions;
import com.github.saphyra.apphub.integration.backend.model.ModulesResponse;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_INVALID_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

public class ModulesTest extends TestBase {
    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void getModules(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(locale, registrationRequest);
        UUID accessTokenId = IndexPageActions.login(
            locale,
            LoginRequest.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .build()
        );

        Map<String, List<ModulesResponse>> result = ModulesPageActions.getModules(locale, accessTokenId);

        assertThat(result).containsOnlyKeys("accounts");
        ModulesResponse expectedModule = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(false)
            .build();
        assertThat(result.get("accounts")).containsExactly(expectedModule);
    }

    @Test(dataProvider = "localeDataProvider")
    public void setAsFavorite_unknownModule(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(locale, registrationRequest);
        UUID accessTokenId = IndexPageActions.login(
            locale,
            LoginRequest.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .build()
        );

        Response response = ModulesPageActions.getSetAsFavoriteResponse(
            locale,
            accessTokenId,
            "unknown-module",
            true
        );

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("module")).isEqualTo("does not exist");
    }

    @Test(dataProvider = "localeDataProvider")
    public void setAsFavorite_nullValue(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(locale, registrationRequest);
        UUID accessTokenId = IndexPageActions.login(
            locale,
            LoginRequest.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .build()
        );

        Response response = ModulesPageActions.getSetAsFavoriteResponse(
            locale,
            accessTokenId,
            "account",
            null
        );

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("value")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void setAsFavorite(Language locale) {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(locale, registrationRequest);
        UUID accessTokenId = IndexPageActions.login(
            locale,
            LoginRequest.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .build()
        );

        Map<String, List<ModulesResponse>> result = ModulesPageActions.setAsFavorite(
            locale,
            accessTokenId,
            "account",
            true
        );

        assertThat(result).containsOnlyKeys("accounts");
        ModulesResponse expectedModule = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(true)
            .build();
        assertThat(result.get("accounts")).containsExactly(expectedModule);
    }
}
