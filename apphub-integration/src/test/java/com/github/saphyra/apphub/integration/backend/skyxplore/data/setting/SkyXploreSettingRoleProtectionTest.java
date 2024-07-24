package com.github.saphyra.apphub.integration.backend.skyxplore.data.setting;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSettingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingIdentifier;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreSettingRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore"})
    public void settingRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> SkyXploreSettingActions.getCreateOrUpdateSettingResponse(accessTokenId, SettingModel.builder().build()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSettingActions.getSettingResponse(accessTokenId, SettingIdentifier.builder().build()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSettingActions.getDeleteResponse(accessTokenId, SettingIdentifier.builder().build()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
