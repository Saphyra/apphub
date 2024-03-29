package com.github.saphyra.apphub.integration.backend.community.friendship;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class FriendshipRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "community"})
    public void friendshipRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> FriendshipActions.getFriendshipsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> FriendshipActions.getDeleteFriendshipResponse(accessTokenId, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_COMMUNITY},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
