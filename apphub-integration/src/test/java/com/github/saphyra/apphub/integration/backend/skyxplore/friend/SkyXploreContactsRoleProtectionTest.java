package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreContactsRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void contactsRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getFriendCandidatesResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getCreateFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getSentFriendRequestsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getIncomingFriendRequestsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getCancelFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getAcceptFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getFriendsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreFriendActions.getRemoveFriendResponse(getServerPort(), accessToken, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
