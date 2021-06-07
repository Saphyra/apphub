package com.github.saphyra.integration.backend.modules;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.ModulesActions;
import com.github.saphyra.apphub.integration.backend.model.ModulesResponse;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

public class ModulesTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
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

        Map<String, List<ModulesResponse>> result = ModulesActions.getModules(locale, accessTokenId);

        assertThat(result).containsKeys("accounts", "office", "development-utils");
        ModulesResponse expectedModuleAccount = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(false)
            .build();
        assertThat(result.get("accounts")).containsExactly(expectedModuleAccount);

        ModulesResponse expectedModuleNotebook = ModulesResponse.builder()
            .name("notebook")
            .url("/web/notebook")
            .favorite(false)
            .build();
        assertThat(result.get("office")).containsExactly(expectedModuleNotebook);

        ModulesResponse expectedModuleLogFormatter = ModulesResponse.builder()
            .name("log-formatter")
            .url("/web/utils/log-formatter")
            .favorite(false)
            .build();
        ModulesResponse expectedModuleJsonFormatter = ModulesResponse.builder()
            .name("json-formatter")
            .url("/web/utils/json-formatter")
            .favorite(false)
            .build();
        assertThat(result.get("development-utils")).containsExactlyInAnyOrder(expectedModuleLogFormatter, expectedModuleJsonFormatter);

        if (!DISABLED_TEST_GROUPS.contains("skyxplore")) {
            assertThat(result).containsKey("game");
            ModulesResponse expectedModuleSkyXplore = ModulesResponse.builder()
                .name("skyxplore")
                .url("/web/skyxplore")
                .favorite(false)
                .build();
            assertThat(result.get("game")).containsExactly(expectedModuleSkyXplore);
        }
    }

    @Test(dataProvider = "languageDataProvider")
    public void setAsFavorite(Language locale) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(locale, userData);

        //Unknown module
        Response unknownModuleResponse = ModulesActions.getSetAsFavoriteResponse(locale, accessTokenId, "unknown-module", true);
        verifyBadRequest(locale, unknownModuleResponse, "module", "does not exist");

        //Favorite null
        Response favoriteNullResponse = ModulesActions.getSetAsFavoriteResponse(locale, accessTokenId, "account", null);
        verifyBadRequest(locale, favoriteNullResponse, "value", "must not be null");

        //Set as favorite
        Map<String, List<ModulesResponse>> setAsFavoriteResponse = ModulesActions.setAsFavorite(locale, accessTokenId, "account", true);

        assertThat(setAsFavoriteResponse).containsKeys("accounts", "office", "development-utils");
        ModulesResponse expectedModule = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(true)
            .build();
        assertThat(setAsFavoriteResponse.get("accounts")).containsExactly(expectedModule);
    }

    private void verifyBadRequest(Language locale, Response unknownModuleResponse, String field, String value) {
        assertThat(unknownModuleResponse.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = unknownModuleResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(locale, INVALID_PARAM));
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }
}
