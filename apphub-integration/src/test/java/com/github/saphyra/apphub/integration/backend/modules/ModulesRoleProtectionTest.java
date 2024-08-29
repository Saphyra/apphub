package com.github.saphyra.apphub.integration.backend.modules;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

public class ModulesRoleProtectionTest extends BackEndTest {
    @Test(groups = {"be", "modules"})
    public void modulesRoleProtection() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), Constants.ROLE_ACCESS);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> ModulesActions.getModulesResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> ModulesActions.getSetAsFavoriteResponse(getServerPort(), accessTokenId, "asd", false));
    }
}
