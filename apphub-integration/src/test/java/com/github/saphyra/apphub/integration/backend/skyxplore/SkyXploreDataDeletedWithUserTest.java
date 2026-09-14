package com.github.saphyra.apphub.integration.backend.skyxplore;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreDataDeletedWithUserTest extends BackEndTest {
    @Test(groups = {"skyxplore", "be"})
    public void skyXploreDataDeletedWithUser() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, SkyXploreCharacterModel.valid());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, SkyXploreCharacterModel.valid());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = UserDynamoDbRepository.getUserIdByEmail(userData3.getEmail());
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, SkyXploreCharacterModel.valid());

        skyXploreTables(accessToken, userId2, accessToken3, userId3);

        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "character", "user_id")).isZero();
            assertThat(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friendship", "friend_2")).isZero();
            assertThat(DatabaseUtil.getRowCountByValue(userId, "skyxplore", "friend_request", "sender_id")).isZero();
        });
    }

    private static void skyXploreTables(String accessToken, UUID userId2, String accessToken3, UUID userId3) {
        //skyxplore.friend_request
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken, userId2);

        //skyxplore.friendship
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken, accessToken3, userId3);
    }
}
