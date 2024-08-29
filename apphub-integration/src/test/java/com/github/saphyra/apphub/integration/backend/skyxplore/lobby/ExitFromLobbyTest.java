package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
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
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId3, characterModel3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        RegistrationParameters userData4 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel4 = SkyXploreCharacterModel.valid();
        UUID accessTokenId4 = IndexPageActions.registerAndLogin(getServerPort(), userData4);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId4, characterModel4);
        UUID userId4 = DatabaseUtil.getUserIdByEmail(userData4.getEmail());

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId1, accessTokenId2, userId2);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId1, accessTokenId3, userId3);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId3, accessTokenId4, userId4);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessTokenId2, accessTokenId4, userId4);

        SkyXploreLobbyActions.createLobby(getServerPort(), accessTokenId1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessTokenId1, userId2);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessTokenId1, userId3);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessTokenId2, userId1);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessTokenId3, userId1);

        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessTokenId2, accessTokenId2);

        ApphubWsClient invitationClient = memberLeft(characterModel3, accessTokenId3, userId3, accessTokenId4, userId4, wsClient);
        hostLeft(characterModel1, accessTokenId1, userId1, accessTokenId2, userId2, userId4, wsClient, invitationClient);
    }

    private static ApphubWsClient memberLeft(SkyXploreCharacterModel characterModel3, UUID accessTokenId3, UUID userId3, UUID accessTokenId4, UUID userId4, ApphubWsClient wsClient) {
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessTokenId3, userId4);
        ApphubWsClient invitationClient = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessTokenId4, accessTokenId4);

        SkyXploreLobbyActions.exitFromLobby(getServerPort(), accessTokenId3);
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

    private static void hostLeft(SkyXploreCharacterModel characterModel1, UUID accessTokenId1, UUID userId1, UUID accessTokenId2, UUID userId2, UUID userId4, ApphubWsClient wsClient, ApphubWsClient mainMenuClient) {
        WebSocketEvent rejectInvitationEvent;
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessTokenId2, userId4);

        SkyXploreLobbyActions.exitFromLobby(getServerPort(), accessTokenId1);
        WebSocketEvent hostLeftEvent = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_EXIT)
            .orElseThrow(() -> new RuntimeException("WebSocket event did not arrive"));
        ExitFromLobbyWsMessage hostLeftMessage = hostLeftEvent.getPayloadAs(ExitFromLobbyWsMessage.class);
        assertThat(hostLeftMessage.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(hostLeftMessage.getUserId()).isEqualTo(userId1);
        assertThat(hostLeftMessage.isHost()).isTrue();
        Response response = SkyXploreLobbyActions.getLobbyPlayersResponse(getServerPort(), accessTokenId2);
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LOBBY_NOT_FOUND.name());

        rejectInvitationEvent = mainMenuClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION)
            .orElseThrow(() -> new RuntimeException("RejectInvitation event did not arrive."));
        assertThat(rejectInvitationEvent.getPayload()).isEqualTo(userId2.toString());

        ApphubWsClient.cleanUpConnections();
    }
}
