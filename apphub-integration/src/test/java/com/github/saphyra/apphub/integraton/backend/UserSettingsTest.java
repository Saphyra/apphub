package com.github.saphyra.apphub.integraton.backend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UserSettingsActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsTest extends BackEndTest {

    @Test(dataProvider = "languageDataProvider", groups = {"be", "misc"})
    public void userSettingsTest(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        query_categoryNotFound(language, accessTokenId);
        query_defaults(language, accessTokenId);
        update_blankKey(language, accessTokenId);
        update_categoryNotFound(language, accessTokenId);
        update_keyNotSupported(language, accessTokenId);
        update(language, accessTokenId);
    }

    private static void query_categoryNotFound(Language language, UUID accessTokenId) {
        Response query_categoryNotFoundResponse = UserSettingsActions.getQueryUserSettingsResponse(language, accessTokenId, "asd");

        ResponseValidator.verifyErrorResponse(query_categoryNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void query_defaults(Language language, UUID accessTokenId) {
        Map<String, String> settings = UserSettingsActions.getUserSettings(language, accessTokenId, Constants.USER_SETTING_CATEGORY_NOTEBOOK);

        assertThat(settings).containsEntry(Constants.USER_SETTING_KEY_SHOW_ARCHIVED, String.valueOf(true));
    }

    private static void update_blankKey(Language language, UUID accessTokenId) {
        SetUserSettingsRequest blankKeyRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key(" ")
            .value("asd")
            .build();

        Response update_blankKeyResponse = UserSettingsActions.getUpdateUserSettingsResponse(language, accessTokenId, blankKeyRequest);

        ResponseValidator.verifyInvalidParam(update_blankKeyResponse, "key", "must not be null or blank");
    }

    private static void update_categoryNotFound(Language language, UUID accessTokenId) {
        SetUserSettingsRequest categoryNotFoundRequest = SetUserSettingsRequest.builder()
            .category("asd")
            .key(Constants.USER_SETTING_KEY_SHOW_ARCHIVED)
            .value("asd")
            .build();

        Response update_categoryNotFoundResponse = UserSettingsActions.getUpdateUserSettingsResponse(language, accessTokenId, categoryNotFoundRequest);

        ResponseValidator.verifyErrorResponse(update_categoryNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void update_keyNotSupported(Language language, UUID accessTokenId) {
        SetUserSettingsRequest unsupportedKeyRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key("asd")
            .value("asd")
            .build();

        Response update_keyNotSupportedResponse = UserSettingsActions.getUpdateUserSettingsResponse(language, accessTokenId, unsupportedKeyRequest);

        ResponseValidator.verifyInvalidParam(update_keyNotSupportedResponse, "key", "not supported");
    }

    private static void update(Language language, UUID accessTokenId) {
        SetUserSettingsRequest setUserSettingsRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key(Constants.USER_SETTING_KEY_SHOW_ARCHIVED)
            .value(String.valueOf(false))
            .build();

        Map<String, String> modifiedSettings = UserSettingsActions.setUserSetting(language, accessTokenId, setUserSettingsRequest);

        assertThat(modifiedSettings).containsEntry(Constants.USER_SETTING_KEY_SHOW_ARCHIVED, String.valueOf(false));
    }
}
