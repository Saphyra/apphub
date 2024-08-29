package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreContactsRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore"})
    public void contactsRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getFriendCandidatesResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getCreateFriendRequestResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getSentFriendRequestsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getIncomingFriendRequestsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getCancelFriendRequestResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getAcceptFriendRequestResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getFriendsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getRemoveFriendResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
