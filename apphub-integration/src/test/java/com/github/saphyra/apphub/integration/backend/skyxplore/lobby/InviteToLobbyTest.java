package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyInvitationWsMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InviteToLobbyTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void inviteToLobby() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, characterModel3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken3, userId3);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken2, accessToken3, userId3);
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId3);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken3, userId1);

        notFriends(accessToken1, userId2);
        ApphubWsClient wsClient = invitationArrived(characterModel1, accessToken1, userId1, accessToken2, userId2);
        flooding(accessToken1, userId2);
        inviteByDifferentPlayer(userId2, characterModel3, accessToken3, userId3, wsClient);
    }

    private static void notFriends(String accessToken1, UUID userId2) {
        Response notFriendsResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(getServerPort(), accessToken1, userId2);
        assertThat(notFriendsResponse.getStatusCode()).isEqualTo(412);
    }

    private static ApphubWsClient invitationArrived(SkyXploreCharacterModel characterModel1, String accessToken1, UUID userId1, String accessToken2, UUID userId2) {
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);
        ApphubWsClient wsClient = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessToken2, accessToken2);

        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId2);
        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"));
        LobbyInvitationWsMessage payload = event.getPayloadAs(LobbyInvitationWsMessage.class);
        assertThat(payload.getSenderId()).isEqualTo(userId1);
        assertThat(payload.getSenderName()).isEqualTo(characterModel1.getName());
        return wsClient;
    }

    private static void flooding(String accessToken1, UUID userId2) {
        Response floodingResponse = SkyXploreLobbyActions.getInviteToLobbyResponse(getServerPort(), accessToken1, userId2);
        assertThat(floodingResponse.getStatusCode()).isEqualTo(429);
        ErrorResponse errorResponse = floodingResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.TOO_FREQUENT_INVITATIONS.name());
    }

    private static void inviteByDifferentPlayer(UUID userId2, SkyXploreCharacterModel characterModel3, String accessToken3, UUID userId3, ApphubWsClient wsClient) {
        WebSocketEvent event;
        LobbyInvitationWsMessage payload;
        wsClient.clearMessages();
        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken3, userId2);
        event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation not arrived"));
        payload = event.getPayloadAs(LobbyInvitationWsMessage.class);
        assertThat(payload.getSenderId()).isEqualTo(userId3);
        assertThat(payload.getSenderName()).isEqualTo(characterModel3.getName());

        ApphubWsClient.cleanUpConnections();
    }
}
