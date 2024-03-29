package com.github.saphyra.apphub.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class BanRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "admin-panel"})
    public void banRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> BanActions.getBanResponse(accessTokenId, new BanRequest()));
        CommonUtils.verifyMissingRole(() -> BanActions.getRevokeBanResponse(accessTokenId, UUID.randomUUID(), userData.getPassword()));
        CommonUtils.verifyMissingRole(() -> BanActions.getGetBansResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> BanActions.getMarkForDeletionResponse(accessTokenId, UUID.randomUUID(), new MarkUserForDeletionRequest()));
        CommonUtils.verifyMissingRole(() -> BanActions.getUnmarkUserForDeletionResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> BanActions.getSearchResponse(accessTokenId, "asd"));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider(){
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
