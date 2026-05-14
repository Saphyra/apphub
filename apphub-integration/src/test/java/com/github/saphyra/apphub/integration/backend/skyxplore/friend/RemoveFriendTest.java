package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyForbiddenOperation;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveFriendTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void removeFriend() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, model);

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, model3);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, model2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        friendNotFound(accessToken1);
        UUID friendshipId = forbiddenOperation(accessToken1, accessToken2, accessToken3, userId2);
        remove(accessToken1, accessToken2, friendshipId);
    }

    private static void friendNotFound(String accessToken1) {
        Response friendNotFoundResponse = SkyXploreFriendActions.getRemoveFriendResponse(getServerPort(), accessToken1, UUID.randomUUID());
        verifyErrorResponse(friendNotFoundResponse, 404, ErrorCode.FRIENDSHIP_NOT_FOUND);
    }

    private static UUID forbiddenOperation(String accessToken1, String accessToken2, String accessToken3, UUID userId2) {
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken1, userId2);
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(getServerPort(), accessToken2, friendRequestId);

        UUID friendshipId = SkyXploreFriendActions.getFriends(getServerPort(), accessToken1)
            .stream()
            .map(FriendshipResponse::getFriendshipId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friendship not found"));

        Response forbiddenOperationResponse = SkyXploreFriendActions.getRemoveFriendResponse(getServerPort(), accessToken3, friendshipId);
        verifyForbiddenOperation(forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessToken1)).hasSize(1);
        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessToken2)).hasSize(1);
        return friendshipId;
    }

    private static void remove(String accessToken1, String accessToken2, UUID friendshipId) {
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(getServerPort(), accessToken2, accessToken2);

        SkyXploreFriendActions.removeFriend(getServerPort(), accessToken1, friendshipId);

        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessToken1)).isEmpty();
        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessToken2)).isEmpty();

        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED).get().getPayloadAs(UUID.class)).isEqualTo(friendshipId);

        ApphubWsClient.cleanUpConnections();
    }
}
