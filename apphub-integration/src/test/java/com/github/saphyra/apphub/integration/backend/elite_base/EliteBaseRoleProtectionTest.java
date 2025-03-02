package com.github.saphyra.apphub.integration.backend.elite_base;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.elite_base.EliteBaseNearestActions;
import com.github.saphyra.apphub.integration.action.backend.elite_base.EliteBaseStarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.elite_base.MaterialType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class EliteBaseRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "elite-base"})
    public void eliteBaseRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> EliteBaseNearestActions.getNearestMaterialTradersResponse(getServerPort(), accessTokenId, UUID.randomUUID(), MaterialType.ENCODED, 0));
        CommonUtils.verifyMissingRole(() -> EliteBaseStarSystemActions.getSearchResponse(getServerPort(), accessTokenId, null));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ELITE_BASE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
