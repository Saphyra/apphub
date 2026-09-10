package com.github.saphyra.apphub.integration.backend.community.group;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMemberRoleRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class GroupRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "community", "role-protection"})
    public void groupRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        CommonUtils.verifyMissingRole(() -> GroupActions.getGroupsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> GroupActions.getCreateGroupResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> GroupActions.getDeleteGroupResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getChangeOwnerResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getRenameGroupResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> GroupActions.getChangeInvitationTypeResponse(getServerPort(), accessToken, UUID.randomUUID(), GroupInvitationType.FRIENDS));
        CommonUtils.verifyMissingRole(() -> GroupActions.getMembersResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getSearchResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> GroupActions.getCreateGroupMemberResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getDeleteGroupMemberResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getModifyRolesResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), new GroupMemberRoleRequest()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_COMMUNITY},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
