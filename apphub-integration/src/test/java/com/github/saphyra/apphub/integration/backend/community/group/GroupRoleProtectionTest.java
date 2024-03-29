package com.github.saphyra.apphub.integration.backend.community.group;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMemberRoleRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class GroupRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "community"})
    public void groupRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> GroupActions.getGroupsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> GroupActions.getCreateGroupResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> GroupActions.getDeleteGroupResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getChangeOwnerResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getRenameGroupResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> GroupActions.getChangeInvitationTypeResponse(accessTokenId, UUID.randomUUID(), GroupInvitationType.FRIENDS));
        CommonUtils.verifyMissingRole(() -> GroupActions.getMembersResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getSearchResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> GroupActions.getCreateGroupMemberResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getDeleteGroupMemberResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> GroupActions.getModifyRolesResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID(), new GroupMemberRoleRequest()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_COMMUNITY},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
