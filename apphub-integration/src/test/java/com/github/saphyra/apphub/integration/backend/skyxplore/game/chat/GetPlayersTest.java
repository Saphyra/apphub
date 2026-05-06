package com.github.saphyra.apphub.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.InvitationMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerStatus;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetPlayersTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void getPlayers() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, characterModel2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, characterModel3);

        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);
        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken3, userId3);

        ApphubWsClient invitationWsClient2 = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessToken2, accessToken2);
        ApphubWsClient invitationWsClient3 = ApphubWsClient.createSkyXploreLobbyInvitation(getServerPort(), accessToken3, accessToken3);

        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);

        List<FriendshipResponse> friends = SkyXploreFriendActions.getFriends(getServerPort(), accessToken1);

        friends.stream()
            .map(FriendshipResponse::getFriendId)
            .forEach(friendId -> SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, friendId));

        acceptInvitation(accessToken2, invitationWsClient2);
        acceptInvitation(accessToken3, invitationWsClient3);

        ApphubWsClient lobbyWsClient1 = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken1, accessToken1);
        ApphubWsClient lobbyWsClient2 = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken2, accessToken2);
        ApphubWsClient lobbyWsClient3 = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken3, accessToken3);

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();

        lobbyWsClient1.send(readyEvent);
        lobbyWsClient2.send(readyEvent);
        lobbyWsClient3.send(readyEvent);

        assertThat(lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, event -> isMemberReady(userId1, event))).isPresent();
        assertThat(lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, event -> isMemberReady(userId2, event))).isPresent();
        assertThat(lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, event -> isMemberReady(userId3, event))).isPresent();

        SkyXploreLobbyActions.startGame(getServerPort(), accessToken1);

        lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);

        ApphubWsClient.createSkyXploreGameMain(getServerPort(), accessToken2, accessToken2);

        List<SkyXploreCharacterModel> characters = SkyXploreGameChatActions.getPlayers(getServerPort(), accessToken1);

        assertThat(characters).hasSize(1);
        assertThat(characters.get(0).getName()).isEqualTo(characterModel2.getName());

        ApphubWsClient.cleanUpConnections();
    }

    private boolean isMemberReady(UUID userId, WebSocketEvent event) {
        LobbyPlayerResponse response = event.getPayloadAs(LobbyPlayerResponse.class);

        return response.getUserId().equals(userId) && response.getStatus() == LobbyPlayerStatus.READY;
    }

    private void acceptInvitation(String accessToken, ApphubWsClient wsClient) {
        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation did not arrive."));

        InvitationMessage invitationMessage = event.getPayloadAs(InvitationMessage.class);

        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken, invitationMessage.getSenderId());
    }
}
