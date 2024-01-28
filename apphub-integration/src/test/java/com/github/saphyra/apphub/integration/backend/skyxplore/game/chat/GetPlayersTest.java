package com.github.saphyra.apphub.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameChatActions;
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
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId1, characterModel1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId2, characterModel2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId3, characterModel3);

        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        SkyXploreFriendActions.setUpFriendship(accessTokenId1, accessTokenId2, userId2);
        SkyXploreFriendActions.setUpFriendship(accessTokenId1, accessTokenId3, userId3);

        ApphubWsClient invitationWsClient2 = ApphubWsClient.createSkyXploreLobbyInvitation(accessTokenId2, accessTokenId2);
        ApphubWsClient invitationWsClient3 = ApphubWsClient.createSkyXploreLobbyInvitation(accessTokenId3, accessTokenId3);

        SkyXploreLobbyActions.createLobby(accessTokenId1, GAME_NAME);

        List<FriendshipResponse> friends = SkyXploreFriendActions.getFriends(accessTokenId1);

        friends.stream()
            .map(FriendshipResponse::getFriendId)
            .forEach(friendId -> SkyXploreLobbyActions.inviteToLobby(accessTokenId1, friendId));

        acceptInvitation(accessTokenId2, invitationWsClient2);
        acceptInvitation(accessTokenId3, invitationWsClient3);

        ApphubWsClient lobbyWsClient1 = ApphubWsClient.createSkyXploreLobby(accessTokenId1, accessTokenId1);
        ApphubWsClient lobbyWsClient2 = ApphubWsClient.createSkyXploreLobby(accessTokenId2, accessTokenId2);
        ApphubWsClient lobbyWsClient3 = ApphubWsClient.createSkyXploreLobby(accessTokenId3, accessTokenId3);

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

        SkyXploreLobbyActions.startGame(accessTokenId1);

        lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);

        ApphubWsClient.createSkyXploreGameMain(accessTokenId2, accessTokenId2);

        List<SkyXploreCharacterModel> characters = SkyXploreGameChatActions.getPlayers(accessTokenId1);

        assertThat(characters).hasSize(1);
        assertThat(characters.get(0).getName()).isEqualTo(characterModel2.getName());

        ApphubWsClient.cleanUpConnections();
    }

    private boolean isMemberReady(UUID userId, WebSocketEvent event) {
        LobbyPlayerResponse response = event.getPayloadAs(LobbyPlayerResponse.class);

        return response.getUserId().equals(userId) && response.getStatus() == LobbyPlayerStatus.READY;
    }

    private void acceptInvitation(UUID accessTokenId, ApphubWsClient wsClient) {
        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation did not arrive."));

        InvitationMessage invitationMessage = event.getPayloadAs(InvitationMessage.class);

        SkyXploreLobbyActions.acceptInvitation(accessTokenId, invitationMessage.getSenderId());
    }
}
