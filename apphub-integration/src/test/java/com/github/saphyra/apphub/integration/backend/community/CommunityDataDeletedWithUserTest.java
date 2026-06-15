package com.github.saphyra.apphub.integration.backend.community;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class CommunityDataDeletedWithUserTest extends BackEndTest {
    private static final String GROUP_NAME = "group-name";
    private static final Map<String, String> COMMUNITY_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("community", "blacklist"),
        new BiWrapper<>("community", "community_group"),
        new BiWrapper<>("community", "community_group_member"),
        new BiWrapper<>("community", "friend_request"),
        new BiWrapper<>("community", "friendship")
    );

    @Test(groups = {"be", "community"})
    public void communityDataDeletedWithUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DynamoDbUtil.getUserIdByEmail(userData.getEmail());

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), adminUserData);
        UUID adminUserId = DynamoDbUtil.getUserIdByEmail(adminUserData.getEmail());
        DynamoDbUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = DynamoDbUtil.getUserIdByEmail(userData3.getEmail());

        communityTables(accessToken, adminUserId, userId2, accessToken3, userId3);

        verifyRecords(userId, rowCount -> rowCount > 0);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifyRecords(userId, integer -> integer == 0));
    }

    private static void communityTables(String accessToken, UUID adminUserId, UUID userId2, String accessToken3, UUID userId3) {
        //community.blacklist
        BlacklistActions.createBlacklist(getServerPort(), accessToken, userId2);

        //community.community_group
        GroupActions.createGroup(getServerPort(), accessToken, GROUP_NAME);

        //community.friend_request
        FriendRequestActions.createFriendRequest(getServerPort(), accessToken, adminUserId);

        //community.friendship
        FriendRequestActions.createFriendRequest(getServerPort(), accessToken, userId3);
        FriendRequestActions.acceptFriendRequest(getServerPort(), accessToken3, FriendRequestActions.getReceivedFriendRequests(getServerPort(), accessToken3).getFirst().getFriendRequestId());
    }

    private void verifyRecords(UUID userId, Predicate<Integer> validator) {
        CommunityDataDeletedWithUserTest.COMMUNITY_TABLES.forEach((schema, tableName) -> {
            if (!validator.test(DatabaseUtil.getRowCountByValue(userId, schema, tableName, "user_id"))) {
                throw new AssertionError("Failed verification for schema " + schema + " table " + tableName);
            }
        });
    }
}
