package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.UserSettingsActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsTest extends BackEndTest {
    @Test(groups = {"be", "misc"})
    public void userSettingsRoleProtection() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> UserSettingsActions.getQueryUserSettingsResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> UserSettingsActions.getUpdateUserSettingsResponse(getServerPort(), accessToken, new SetUserSettingsRequest()));
    }


    @Test(groups = {"be", "misc"})
    public void userSettingsTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        query_categoryNotFound(accessToken);
        query_defaults(accessToken);
        update_blankKey(accessToken);
        update_categoryNotFound(accessToken);
        update_keyNotSupported(accessToken);
        update(accessToken);
    }

    private static void query_categoryNotFound(String accessToken) {
        Response query_categoryNotFoundResponse = UserSettingsActions.getQueryUserSettingsResponse(getServerPort(), accessToken, "asd");

        ResponseValidator.verifyErrorResponse(query_categoryNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void query_defaults(String accessToken) {
        Map<String, String> settings = UserSettingsActions.getUserSettings(getServerPort(), accessToken, Constants.USER_SETTING_CATEGORY_NOTEBOOK);

        assertThat(settings).containsEntry(Constants.USER_SETTING_KEY_SHOW_ARCHIVED, String.valueOf(true));
    }

    private static void update_blankKey(String accessToken) {
        SetUserSettingsRequest blankKeyRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key(" ")
            .value("asd")
            .build();

        Response update_blankKeyResponse = UserSettingsActions.getUpdateUserSettingsResponse(getServerPort(), accessToken, blankKeyRequest);

        ResponseValidator.verifyInvalidParam(update_blankKeyResponse, "key", "must not be null or blank");
    }

    private static void update_categoryNotFound(String accessToken) {
        SetUserSettingsRequest categoryNotFoundRequest = SetUserSettingsRequest.builder()
            .category("asd")
            .key(Constants.USER_SETTING_KEY_SHOW_ARCHIVED)
            .value("asd")
            .build();

        Response update_categoryNotFoundResponse = UserSettingsActions.getUpdateUserSettingsResponse(getServerPort(), accessToken, categoryNotFoundRequest);

        ResponseValidator.verifyErrorResponse(update_categoryNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void update_keyNotSupported(String accessToken) {
        SetUserSettingsRequest unsupportedKeyRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key("asd")
            .value("asd")
            .build();

        Response update_keyNotSupportedResponse = UserSettingsActions.getUpdateUserSettingsResponse(getServerPort(), accessToken, unsupportedKeyRequest);

        ResponseValidator.verifyInvalidParam(update_keyNotSupportedResponse, "key", "not supported");
    }

    private static void update(String accessToken) {
        SetUserSettingsRequest setUserSettingsRequest = SetUserSettingsRequest.builder()
            .category(Constants.USER_SETTING_CATEGORY_NOTEBOOK)
            .key(Constants.USER_SETTING_KEY_SHOW_ARCHIVED)
            .value(String.valueOf(false))
            .build();

        Map<String, String> modifiedSettings = UserSettingsActions.setUserSetting(getServerPort(), accessToken, setUserSettingsRequest);

        assertThat(modifiedSettings).containsEntry(Constants.USER_SETTING_KEY_SHOW_ARCHIVED, String.valueOf(false));
    }
}
