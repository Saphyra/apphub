package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UserSettingsActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsTest extends BackEndTest {
    @Test(groups = {"be", "misc"})
    public void userSettingsRoleProtection() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> UserSettingsActions.getQueryUserSettingsResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> UserSettingsActions.getUpdateUserSettingsResponse(accessTokenId, new SetUserSettingsRequest()));
    }


    @Test(groups = {"be", "misc"})
    public void userSettingsTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        query_categoryNotFound(accessTokenId);
        query_defaults(accessTokenId);
        update_blankKey(accessTokenId);
        update_categoryNotFound(accessTokenId);
        update_keyNotSupported(accessTokenId);
        update(accessTokenId);
    }

    private static void query_categoryNotFound(UUID accessTokenId) {
        Response query_categoryNotFoundResponse = UserSettingsActions.getQueryUserSettingsResponse(accessTokenId, "asd");

        ResponseValidator.verifyErrorResponse(query_categoryNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void query_defaults(UUID accessTokenId) {
        Map<String, String> settings = UserSettingsActions.getUserSettings(accessTokenId, Constants.USER_SETTING_CATEGORY_NOTEBOOK);

        assertThat(settings).containsEntry(Constants.USER_SETTING_KEY_SHOW_ARCHIVED, String.valueOf(true));
    }

    private static void update_blankKey(UUID accessTokenId) {
        SetUserSettingsRequest blankKeyRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key(" ")
            .value("asd")
            .build();

        Response update_blankKeyResponse = UserSettingsActions.getUpdateUserSettingsResponse(accessTokenId, blankKeyRequest);

        ResponseValidator.verifyInvalidParam(update_blankKeyResponse, "key", "must not be null or blank");
    }

    private static void update_categoryNotFound(UUID accessTokenId) {
        SetUserSettingsRequest categoryNotFoundRequest = SetUserSettingsRequest.builder()
            .category("asd")
            .key(Constants.USER_SETTING_KEY_SHOW_ARCHIVED)
            .value("asd")
            .build();

        Response update_categoryNotFoundResponse = UserSettingsActions.getUpdateUserSettingsResponse(accessTokenId, categoryNotFoundRequest);

        ResponseValidator.verifyErrorResponse(update_categoryNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void update_keyNotSupported(UUID accessTokenId) {
        SetUserSettingsRequest unsupportedKeyRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key("asd")
            .value("asd")
            .build();

        Response update_keyNotSupportedResponse = UserSettingsActions.getUpdateUserSettingsResponse(accessTokenId, unsupportedKeyRequest);

        ResponseValidator.verifyInvalidParam(update_keyNotSupportedResponse, "key", "not supported");
    }

    private static void update(UUID accessTokenId) {
        SetUserSettingsRequest setUserSettingsRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key(Constants.USER_SETTING_KEY_SHOW_ARCHIVED)
            .value(String.valueOf(false))
            .build();

        Map<String, String> modifiedSettings = UserSettingsActions.setUserSetting(accessTokenId, setUserSettingsRequest);

        assertThat(modifiedSettings).containsEntry(Constants.USER_SETTING_KEY_SHOW_ARCHIVED, String.valueOf(false));
    }
}
