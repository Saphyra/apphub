package com.github.saphyra.apphub.integration.backend.modules;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.structure.api.ModulesResponse;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.core.TestConfiguration.DISABLED_TEST_GROUPS;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class ModulesTest extends BackEndTest {
    @Test(groups = {"be", "modules"})
    public void getModules() {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(registrationRequest);
        UUID accessTokenId = IndexPageActions.login(
            LoginRequest.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .build()
        );

        Map<String, List<ModulesResponse>> result = ModulesActions.getModules(accessTokenId);

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
        assertThat(result.get("office")).contains(expectedModuleNotebook);

        ModulesResponse expectedModuleCalendar = ModulesResponse.builder()
            .name("calendar")
            .url("/web/calendar")
            .favorite(false)
            .build();
        assertThat(result.get("office")).contains(expectedModuleCalendar);

        ModulesResponse expectedModuleHtmlTraining = ModulesResponse.builder()
            .name("html")
            .url("/web/training/html/001_introduction")
            .favorite(false)
            .build();
        ModulesResponse expectedModuleCssTraining = ModulesResponse.builder()
            .name("css")
            .url("/web/training/css/001_introduction")
            .favorite(false)
            .build();
        ModulesResponse expectedModuleBasicsOfProgrammingTraining = ModulesResponse.builder()
            .name("basics-of-programming")
            .url("/web/training/basics_of_programming/001_introduction")
            .favorite(false)
            .build();
        ModulesResponse expectedModuleJavaScript = ModulesResponse.builder()
            .name("javascript")
            .url("/web/training/javascript/001_introduction")
            .favorite(false)
            .build();
        assertThat(result.get("training")).containsExactlyInAnyOrder(expectedModuleHtmlTraining, expectedModuleCssTraining, expectedModuleBasicsOfProgrammingTraining, expectedModuleJavaScript);

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
        assertThat(result.get("development-utils")).containsExactlyInAnyOrder(expectedModuleLogFormatter, expectedModuleJsonFormatter, expectedModuleBase64Encoder);

        if (!DISABLED_TEST_GROUPS.contains("skyxplore")) {
            assertThat(result).containsKey("game");
            ModulesResponse expectedModuleSkyXplore = ModulesResponse.builder()
                .name("skyxplore")
                .url("/web/skyxplore")
                .favorite(false)
                .build();
            assertThat(result.get("game")).containsExactly(expectedModuleSkyXplore);
        }

        if (!DISABLED_TEST_GROUPS.contains("community")) {
            assertThat(result).containsKey("community");
            ModulesResponse expectedModuleCommunity = ModulesResponse.builder()
                .name("community")
                .url("/web/community")
                .favorite(false)
                .build();
            assertThat(result.get("community")).containsExactly(expectedModuleCommunity);
        }
    }

    @Test(groups = {"be", "modules"})
    public void setAsFavorite() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        unknownModule(accessTokenId);
        favoriteNull(accessTokenId);
        setAsFavorite(accessTokenId);
    }

    private static void unknownModule(UUID accessTokenId) {
        Response unknownModuleResponse = ModulesActions.getSetAsFavoriteResponse(accessTokenId, "unknown-module", true);
        verifyInvalidParam(unknownModuleResponse, "module", "does not exist");
    }

    private static void favoriteNull(UUID accessTokenId) {
        Response favoriteNullResponse = ModulesActions.getSetAsFavoriteResponse(accessTokenId, "account", null);
        verifyInvalidParam(favoriteNullResponse, "value", "must not be null");
    }

    private static void setAsFavorite(UUID accessTokenId) {
        Map<String, List<ModulesResponse>> setAsFavoriteResponse = ModulesActions.setAsFavorite(accessTokenId, "account", true);

        assertThat(setAsFavoriteResponse).containsKeys("accounts", "office", "development-utils");
        ModulesResponse expectedModule = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(true)
            .build();
        assertThat(setAsFavoriteResponse.get("accounts")).containsExactly(expectedModule);
    }
}
