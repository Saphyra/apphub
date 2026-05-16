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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyForbiddenOperation;
import static org.assertj.core.api.Assertions.assertThat;

public class AcceptFriendRequestTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void acceptFriendRequest() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);

        SkyXploreCharacterModel model1 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, model1);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, model2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, model3);

        friendRequestNotFound(accessToken1);
        UUID friendRequestId = forbiddenOperation(accessToken1, accessToken3, userId2);
        accept(accessToken1, userId1, accessToken2, model1, model2, userId2, friendRequestId);
    }

    private static void friendRequestNotFound(String accessToken1) {
        Response friendRequestNotFoundResponse = SkyXploreFriendActions.getAcceptFriendRequestResponse(getServerPort(), accessToken1, UUID.randomUUID());
        verifyErrorResponse(friendRequestNotFoundResponse, 404, ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    private static UUID forbiddenOperation(String accessToken1, String accessToken3, UUID userId2) {
        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken1, userId2);

        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        Response forbiddenOperationResponse = SkyXploreFriendActions.getAcceptFriendRequestResponse(getServerPort(), accessToken3, friendRequestId);
        verifyForbiddenOperation(forbiddenOperationResponse);
        return friendRequestId;
    }

    private static void accept(String accessToken1, UUID userId1, String accessToken2, SkyXploreCharacterModel model1, SkyXploreCharacterModel model2, UUID userId2, UUID friendRequestId) {
        ApphubWsClient senderClient = ApphubWsClient.createSkyXploreMainMenu(getServerPort(), accessToken1, accessToken1);

        FriendshipResponse acceptResponse = SkyXploreFriendActions.acceptFriendRequest(getServerPort(), accessToken2, friendRequestId);

        assertThat(acceptResponse.getFriendName()).isEqualTo(model1.getName());
        assertThat(acceptResponse.getFriendId()).isEqualTo(userId1);

        assertThat(SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1)).isEmpty();
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(getServerPort(), accessToken2)).isEmpty();

        List<FriendshipResponse> senderFriendships = SkyXploreFriendActions.getFriends(getServerPort(), accessToken1);
        assertThat(senderFriendships).hasSize(1);
        assertThat(senderFriendships.get(0).getFriendName()).isEqualTo(model2.getName());
        List<FriendshipResponse> receiverFriendships = SkyXploreFriendActions.getFriends(getServerPort(), accessToken2);
        assertThat(receiverFriendships).hasSize(1);
        assertThat(receiverFriendships.get(0).getFriendName()).isEqualTo(model1.getName());

        WsEventPayload payload = senderClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_ACCEPTED).get().getPayloadAs(WsEventPayload.class);
        assertThat(payload.getFriendRequestId()).isEqualTo(friendRequestId);
        assertThat(payload.getFriendship().getFriendId()).isEqualTo(userId2);
        assertThat(payload.getFriendship().getFriendName()).isEqualTo(model2.getName());
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    private static class WsEventPayload {
        private UUID friendRequestId;
        private FriendshipResponse friendship;
    }
}
