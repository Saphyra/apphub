package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.action.backend.UserSettingsActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class DataDeletedWithUserTest extends BackEndTest {
    private static final String REASON = "reason";
    private static final String GROUP_NAME = "group-name";

    private static final Map<String, String> GENERIC_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("apphub_user", "settings"),
        new BiWrapper<>("modules", "favorite")
    );

    private static final Map<String, String> COMMUNITY_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("community", "blacklist"),
        new BiWrapper<>("community", "community_group"),
        new BiWrapper<>("community", "community_group_member"),
        new BiWrapper<>("community", "friend_request"),
        new BiWrapper<>("community", "friendship")
    );

    private static final Map<String, String> SKYXPLORE_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("skyxplore", "character")
    );

    @Test(groups = {"be", "misc"})
    public void dataDeletedWithTheUser() {
        //apphub_user.access_token
        //apphub_user.apphub_role
        //apphub_user.apphub_user
        //skyxplore.character
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DynamoDbUtil.getUserIdByEmail(userData.getEmail());

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), adminUserData.toRegistrationRequest());
        DynamoDbUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), adminUserData.toLoginRequest());
        String adminAccessToken = tokenResponse.getAccessToken()
            .getJwt();

        createRecords(accessToken, userId, adminUserData, adminAccessToken);

        verifyRecords(GENERIC_TABLES, userId, rowCount -> rowCount > 0);

        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifyRecords(GENERIC_TABLES, userId, integer -> integer == 0));
    }

    @Test(groups = {"be", "community"})
    public void communityDataDeletedWithTheUser() {
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

        verifyRecords(COMMUNITY_TABLES, userId, rowCount -> rowCount > 0);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifyRecords(COMMUNITY_TABLES, userId, integer -> integer == 0));
    }

    @Test(groups = {"be", "skyxplore"})
    public void skyXploreDataDeletedWithTheUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DynamoDbUtil.getUserIdByEmail(userData.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, SkyXploreCharacterModel.valid());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, SkyXploreCharacterModel.valid());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = DynamoDbUtil.getUserIdByEmail(userData3.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, SkyXploreCharacterModel.valid());

        skyXploreTables(accessToken, userId2, accessToken3, userId3);

        verifySkyXploreRecords(userId, rowCount -> rowCount > 0);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> verifySkyXploreRecords(userId, integer -> integer == 0));
    }

    private void verifyRecords(Map<String, String> tables, UUID userId, Predicate<Integer> validator) {
        tables.forEach((schema, tableName) -> {
            if (!validator.test(DatabaseUtil.getRowCountByValue(userId, schema, tableName, "user_id"))) {
                throw new AssertionError("Failed verification for schema " + schema + " table " + tableName);
            }
        });
    }

    private void verifySkyXploreRecords(UUID userId, Predicate<Integer> validator) {
        SKYXPLORE_TABLES.forEach((schema, tableName) -> assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, schema, tableName, "user_id"))).isTrue());

        assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friendship", "friend_2"))).isTrue();
        assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friend_request", "sender_id"))).isTrue();
    }

    private void createRecords(String accessToken, UUID userId, RegistrationParameters adminUserData, String adminAccessToken) {
        modulesTables(accessToken);
        apphubUserTables(accessToken, userId, adminUserData, adminAccessToken);
    }

    private static void skyXploreTables(String accessToken, UUID userId2, String accessToken3, UUID userId3) {
        //skyxplore.friend_request
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken, userId2);

        //skyxplore.friendship
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken, accessToken3, userId3);
    }

    private static void modulesTables(String accessToken) {
        //modules.favorite
        ModulesActions.setAsFavorite(getServerPort(), accessToken, "notebook", true);
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

    private static void apphubUserTables(String accessToken, UUID userId, RegistrationParameters adminUserData, String adminAccessToken) {
        //apphub_user.settings
        SetUserSettingsRequest setUserSettingsRequest = SetUserSettingsRequest.builder()
            .category("notebook")
            .key("show-archived")
            .value("true")
            .build();
        UserSettingsActions.setUserSetting(getServerPort(), accessToken, setUserSettingsRequest);

        //apphub_user.ban
        BanRequest banRequest = BanRequest.builder()
            .bannedUserId(userId)
            .bannedRole(Constants.ROLE_TRAINING)
            .permanent(true)
            .reason(REASON)
            .password(adminUserData.getPassword())
            .build();
        BanActions.ban(getServerPort(), adminAccessToken, banRequest);
    }
}
