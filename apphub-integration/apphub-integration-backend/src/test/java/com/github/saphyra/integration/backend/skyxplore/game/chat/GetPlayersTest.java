package com.github.saphyra.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.InvitationMessage;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ReadinessEvent;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.SkyXploreGameWsClient;
import com.github.saphyra.apphub.integration.backend.ws.SkyXploreLobbyWsClient;
import com.github.saphyra.apphub.integration.backend.ws.SkyXploreMainMenuWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetPlayersTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test
    public void getPlayers() throws URISyntaxException {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, characterModel2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel3 = SkyXploreCharacterModel.valid();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, characterModel3);

        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, characterModel2.getName());
        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId3, characterModel3.getName());

        SkyXploreMainMenuWsClient mainMenuWsClient2 = new SkyXploreMainMenuWsClient(language, accessTokenId2);
        SkyXploreMainMenuWsClient mainMenuWsClient3 = new SkyXploreMainMenuWsClient(language, accessTokenId3);

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        List<FriendshipResponse> friends = SkyXploreFriendActions.getFriends(language, accessTokenId1);

        friends.stream()
            .map(FriendshipResponse::getFriendId)
            .forEach(friendId -> SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, friendId));

        acceptInvitation(language, accessTokenId2, mainMenuWsClient2);
        acceptInvitation(language, accessTokenId3, mainMenuWsClient3);

        SkyXploreLobbyWsClient lobbyWsClient1 = new SkyXploreLobbyWsClient(language, accessTokenId1);
        SkyXploreLobbyWsClient lobbyWsClient2 = new SkyXploreLobbyWsClient(language, accessTokenId2);
        SkyXploreLobbyWsClient lobbyWsClient3 = new SkyXploreLobbyWsClient(language, accessTokenId3);

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();

        lobbyWsClient1.send(readyEvent);
        lobbyWsClient2.send(readyEvent);
        lobbyWsClient3.send(readyEvent);

        assertThat(lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS, event -> event.getPayloadAs(ReadinessEvent.class).equals(new ReadinessEvent(userId1, true)))).isPresent();
        assertThat(lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS, event -> event.getPayloadAs(ReadinessEvent.class).equals(new ReadinessEvent(userId2, true)))).isPresent();
        assertThat(lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS, event -> event.getPayloadAs(ReadinessEvent.class).equals(new ReadinessEvent(userId3, true)))).isPresent();

        SkyXploreLobbyActions.startGame(language, accessTokenId1);

        lobbyWsClient1.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);

        new SkyXploreGameWsClient(language, accessTokenId2);

        List<SkyXploreCharacterModel> characters = SkyXploreGameChatActions.getPlayers(language, accessTokenId1);

        assertThat(characters).hasSize(1);
        assertThat(characters.get(0).getName()).isEqualTo(characterModel2.getName());
    }

    private void acceptInvitation(Language language, UUID accessTokenId, SkyXploreMainMenuWsClient wsClient) {
        WebSocketEvent event = wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .orElseThrow(() -> new RuntimeException("Invitation did not arrive."));

        InvitationMessage invitationMessage = event.getPayloadAs(InvitationMessage.class);

        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId, invitationMessage.getSenderId());
    }
}
