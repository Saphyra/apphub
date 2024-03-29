package com.github.saphyra.apphub.integration.backend.admin_panel.disabled_role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.DisabledRoleActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class DisabledRoleManagementRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "admin-panel"})
    public void disabledRoleManagementRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> DisabledRoleActions.getGetDisabledRoles(accessTokenId));
        CommonUtils.verifyMissingRole(() -> DisabledRoleActions.getEnableRoleResponse(accessTokenId, userData.getPassword(), Constants.ROLE_TEST));
        CommonUtils.verifyMissingRole(() -> DisabledRoleActions.getDisableRoleResponse(accessTokenId, userData.getPassword(), Constants.ROLE_TEST));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider(){
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
