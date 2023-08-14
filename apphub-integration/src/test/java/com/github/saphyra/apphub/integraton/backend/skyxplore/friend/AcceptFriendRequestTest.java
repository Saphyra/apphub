package com.github.saphyra.apphub.integraton.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void acceptFriendRequest(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);

        SkyXploreCharacterModel model1 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, model1);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, model3);

        //FriendRequest not found
        Response friendRequestNotFoundResponse = SkyXploreFriendActions.getAcceptFriendRequestResponse(language, accessTokenId1, UUID.randomUUID());
        verifyErrorResponse(language, friendRequestNotFoundResponse, 404, ErrorCode.FRIEND_REQUEST_NOT_FOUND);

        //Forbidden operation
        SkyXploreFriendActions.createFriendRequest(language, accessTokenId1, userId2);

        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        Response forbiddenOperationResponse = SkyXploreFriendActions.getAcceptFriendRequestResponse(language, accessTokenId3, friendRequestId);
        verifyForbiddenOperation(language, forbiddenOperationResponse);

        //Accept
        ApphubWsClient senderClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId1);

        FriendshipResponse acceptResponse = SkyXploreFriendActions.acceptFriendRequest(language, accessTokenId2, friendRequestId);

        assertThat(acceptResponse.getFriendName()).isEqualTo(model1.getName());
        assertThat(acceptResponse.getFriendId()).isEqualTo(userId1);

        assertThat(SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)).isEmpty();
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(language, accessTokenId2)).isEmpty();

        List<FriendshipResponse> senderFriendships = SkyXploreFriendActions.getFriends(language, accessTokenId1);
        assertThat(senderFriendships).hasSize(1);
        assertThat(senderFriendships.get(0).getFriendName()).isEqualTo(model2.getName());
        List<FriendshipResponse> receiverFriendships = SkyXploreFriendActions.getFriends(language, accessTokenId2);
        assertThat(receiverFriendships).hasSize(1);
        assertThat(receiverFriendships.get(0).getFriendName()).isEqualTo(model1.getName());

        WsEventPayload payload = senderClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_ACCEPTED).get().getPayloadAs(WsEventPayload.class);
        assertThat(payload.getFriendRequestId()).isEqualTo(friendRequestId);
        assertThat(payload.getFriendship().getFriendId()).isEqualTo(userId2);
        assertThat(payload.getFriendship().getFriendName()).isEqualTo(model2.getName());

        ApphubWsClient.cleanUpConnections();
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
