package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
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
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, model);

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId3, model3);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        friendNotFound(accessTokenId1);
        UUID friendshipId = forbiddenOperation(accessTokenId1, accessTokenId2, accessTokenId3, userId2);
        remove(accessTokenId1, accessTokenId2, friendshipId);
    }

    private static void friendNotFound(UUID accessTokenId1) {
        Response friendNotFoundResponse = SkyXploreFriendActions.getRemoveFriendResponse(getServerPort(), accessTokenId1, UUID.randomUUID());
        verifyErrorResponse(friendNotFoundResponse, 404, ErrorCode.FRIENDSHIP_NOT_FOUND);
    }

    private static UUID forbiddenOperation(UUID accessTokenId1, UUID accessTokenId2, UUID accessTokenId3, UUID userId2) {
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessTokenId1, userId2);
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessTokenId1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(getServerPort(), accessTokenId2, friendRequestId);

        UUID friendshipId = SkyXploreFriendActions.getFriends(getServerPort(), accessTokenId1)
            .stream()
            .map(FriendshipResponse::getFriendshipId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friendship not found"));

        Response forbiddenOperationResponse = SkyXploreFriendActions.getRemoveFriendResponse(getServerPort(), accessTokenId3, friendshipId);
        verifyForbiddenOperation(forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessTokenId1)).hasSize(1);
        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessTokenId2)).hasSize(1);
        return friendshipId;
    }

    private static void remove(UUID accessTokenId1, UUID accessTokenId2, UUID friendshipId) {
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(getServerPort(), accessTokenId2, accessTokenId2);

        SkyXploreFriendActions.removeFriend(getServerPort(), accessTokenId1, friendshipId);

        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessTokenId1)).isEmpty();
        assertThat(SkyXploreFriendActions.getFriends(getServerPort(), accessTokenId2)).isEmpty();

        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED).get().getPayloadAs(UUID.class)).isEqualTo(friendshipId);

        ApphubWsClient.cleanUpConnections();
    }
}
