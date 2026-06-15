package com.github.saphyra.apphub.integration.backend.skyxplore;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreDataDeletedWithUserTest extends BackEndTest {
    private static final Map<String, String> SKYXPLORE_TABLES = CollectionUtils.toMap(
        new BiWrapper<>("skyxplore", "character")
    );

    @Test(groups = {"skyxplore", "be"})
    public void skyXploreDataDeletedWithUser() {
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

    private static void skyXploreTables(String accessToken, UUID userId2, String accessToken3, UUID userId3) {
        //skyxplore.friend_request
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken, userId2);

        //skyxplore.friendship
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken, accessToken3, userId3);
    }

    private void verifySkyXploreRecords(UUID userId, Predicate<Integer> validator) {
        SKYXPLORE_TABLES.forEach((schema, tableName) -> assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, schema, tableName, "user_id"))).isTrue());

        assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friendship", "friend_2"))).isTrue();
        assertThat(validator.test(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friend_request", "sender_id"))).isTrue();
    }
}
