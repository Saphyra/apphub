package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatWsMessageForLobby;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String MESSAGE = "message";

    @Test(groups = "skyxplore")
    public void sendMessage() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(language, accessTokenId1, userId2);
        SkyXploreLobbyActions.acceptInvitation(language, accessTokenId2, userId1);

        ApphubWsClient hostClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId1);
        ApphubWsClient memberClient = ApphubWsClient.createSkyXploreLobby(language, accessTokenId2);

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)
            .payload(MESSAGE)
            .build();
        hostClient.send(event);

        ChatWsMessageForLobby expected = ChatWsMessageForLobby.builder()
            .senderId(userId1)
            .senderName(characterModel1.getName())
            .message(MESSAGE)
            .build();

        assertThat(hostClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE).map(event1 -> event1.getPayloadAs(ChatWsMessageForLobby.class))).contains(expected);
        assertThat(memberClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE).map(event1 -> event1.getPayloadAs(ChatWsMessageForLobby.class))).contains(expected);

        ApphubWsClient.cleanUpConnections();
    }
}
