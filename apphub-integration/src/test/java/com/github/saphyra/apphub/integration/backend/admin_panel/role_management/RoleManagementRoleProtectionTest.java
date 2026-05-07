package com.github.saphyra.apphub.integration.backend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.RoleManagementActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.RoleRequest;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RoleManagementRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "admin-panel", "role-protection"})
    public void roleManagementRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> RoleManagementActions.getRolesResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> RoleManagementActions.getAddRoleResponse(getServerPort(), accessToken, new RoleRequest()));
        CommonUtils.verifyMissingRole(() -> RoleManagementActions.getRemoveRoleResponse(getServerPort(), accessToken, new RoleRequest()));
        CommonUtils.verifyMissingRole(() -> RoleManagementActions.getAddToAllResponse(getServerPort(), accessToken, userData.getPassword(), Constants.ROLE_TEST));
        CommonUtils.verifyMissingRole(() -> RoleManagementActions.getRemoveFromAllResponse(getServerPort(), accessToken, userData.getPassword(), Constants.ROLE_TEST));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
