package com.github.saphyra.apphub.integration.backend.skyxplore.data.setting;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSettingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingIdentifier;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SkyXploreSettingRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void settingRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        CommonUtils.verifyMissingRole(() -> SkyXploreSettingActions.getCreateOrUpdateSettingResponse(getServerPort(), accessToken, SettingModel.builder().build()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSettingActions.getSettingResponse(getServerPort(), accessToken, SettingIdentifier.builder().build()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSettingActions.getDeleteResponse(getServerPort(), accessToken, SettingIdentifier.builder().build()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
