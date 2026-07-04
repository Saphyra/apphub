package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ExitFromLobbyWsMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ExitFromLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void hostLeftTheLobby() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, characterModel2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, characterModel3);
        UUID userId3 = UserDynamoDbRepository.getUserIdByEmail(userData3.getEmail());

        RegistrationParameters userData4 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel4 = SkyXploreCharacterModel.valid();
        String accessToken4 = IndexPageActions.registerAndLogin(getServerPort(), userData4);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken4, characterModel4);
        UUID userId4 = UserDynamoDbRepository.getUserIdByEmail(userData4.getEmail());

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken3, userId3);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken3, accessToken4, userId4);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken2, accessToken4, userId4);

        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId2);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId3);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken2, userId1);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken3, userId1);

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken2, accessToken2);

        ApphubWsClient invitationClient = memberLeft(characterModel3, accessToken3, userId3, accessToken4, userId4, wsClient);
        hostLeft(characterModel1, accessToken1, userId1, accessToken2, userId2, userId4, wsClient, invitationClient);
    }

    private static ApphubWsClient memberLeft(SkyXploreCharacterModel characterModel3, String accessToken3, UUID userId3, String accessToken4, UUID userId4, ApphubWsClient wsClient) {
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken3, userId4);
        ApphubWsClient invitationClient = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessToken4, accessToken4);

        SkyXploreLobbyActions.exitFromLobby(getServerPort(), accessToken3);
        WebSocketEvent memberLeftEvent = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT, webSocketEvent -> webSocketEvent.getPayloadAs(ExitFromLobbyWsMessage.class).getUserId().equals(userId3))
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));
        ExitFromLobbyWsMessage memberLeftMessage = memberLeftEvent.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(memberLeftMessage.getCharacterName()).isEqualTo(characterModel3.getName());
        assertThat(memberLeftMessage.getUserId()).isEqualTo(userId3);
        assertThat(memberLeftMessage.isHost()).isFalse();

        WebSocketEvent rejectInvitationEvent = invitationClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION)
            .orElseThrow(() -> new RuntimeException("RejectInvitation event did not arrive."));
        assertThat(rejectInvitationEvent.getPayload()).isEqualTo(userId3.toString());

        wsClient.clearMessages();
        invitationClient.clearMessages();
        return invitationClient;
    }

    private static void hostLeft(SkyXploreCharacterModel characterModel1, String accessToken1, UUID userId1, String accessToken2, UUID userId2, UUID userId4, ApphubWsClient wsClient, ApphubWsClient mainMenuClient) {
        WebSocketEvent rejectInvitationEvent;
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken2, userId4);

        SkyXploreLobbyActions.exitFromLobby(getServerPort(), accessToken1);
        WebSocketEvent hostLeftEvent = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT)
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));
        ExitFromLobbyWsMessage hostLeftMessage = hostLeftEvent.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(hostLeftMessage.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(hostLeftMessage.getUserId()).isEqualTo(userId1);
        assertThat(hostLeftMessage.isHost()).isTrue();
        Response response = SkyXploreLobbyActions.getLobbyPlayersResponse(getServerPort(), accessToken2);
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_NOT_FOUND.name());

        rejectInvitationEvent = mainMenuClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION)
            .orElseThrow(() -> new RuntimeException("RejectInvitation event did not arrive."));
        assertThat(rejectInvitationEvent.getPayload()).isEqualTo(userId2.toString());

        ApphubWsClient.cleanUpConnections();
    }
}
