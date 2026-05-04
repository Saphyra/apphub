package com.github.saphyra.apphub.integration.backend.community.friend_request;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class FriendRequestRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "community", "role-protection"})
    public void friendRequestRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        CommonUtils.verifyMissingRole(() -> FriendRequestActions.getSearchResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> FriendRequestActions.getSentFriendRequestsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> FriendRequestActions.getReceivedFriendRequeestsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> FriendRequestActions.getCreateFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> FriendRequestActions.getDeleteFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> FriendRequestActions.getAcceptFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_COMMUNITY},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
