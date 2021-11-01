package com.github.saphyra.integration.backend.modules;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.ModulesActions;
import com.github.saphyra.apphub.integration.backend.model.ModulesResponse;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyInvalidParam;
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
        ModulesResponse expectedModuleBase64Encoder = ModulesResponse.builder()
            .name("base64")
            .url("/web/utils/base64")
            .favorite(false)
            .build();
        ModulesResponse expectedModuleTraining = ModulesResponse.builder()
            .name("training")
            .url("/web/training")
            .favorite(false)
            .build();
        assertThat(result.get("development-utils")).containsExactlyInAnyOrder(expectedModuleLogFormatter, expectedModuleJsonFormatter, expectedModuleBase64Encoder, expectedModuleTraining);

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
        verifyInvalidParam(locale, unknownModuleResponse, "module", "does not exist");

        //Favorite null
        Response favoriteNullResponse = ModulesActions.getSetAsFavoriteResponse(locale, accessTokenId, "account", null);
        verifyInvalidParam(locale, favoriteNullResponse, "value", "must not be null");

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
}
