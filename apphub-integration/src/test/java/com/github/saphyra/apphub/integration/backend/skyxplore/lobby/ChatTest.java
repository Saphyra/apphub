package com.github.saphyra.apphub.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatWsMessageForLobby;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String MESSAGE = "message";

    @Test(groups = {"be", "skyxplore"})
    public void sendMessage() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, characterModel2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreFriendActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);

        SkyXploreLobbyActions.createLobby(getServerPort(), accessToken1, GAME_NAME);

        SkyXploreLobbyActions.inviteToLobby(getServerPort(), accessToken1, userId2);
        SkyXploreLobbyActions.acceptInvitation(getServerPort(), accessToken2, userId1);

        ApphubWsClient hostClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken1, accessToken1);
        ApphubWsClient memberClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), accessToken2, accessToken2);

        messageTooLong(hostClient, memberClient);
        sendMessage(hostClient, userId1, characterModel1, memberClient);
    }

    private void messageTooLong(ApphubWsClient hostClient, ApphubWsClient memberClient) {
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)
            .payload(Stream.generate(() -> "a").limit(1025).collect(Collectors.joining()))
            .build();
        hostClient.send(event);

        assertThat(hostClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)).isEmpty();
        assertThat(memberClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)).isEmpty();
    }

    private static void sendMessage(ApphubWsClient hostClient, UUID userId1, SkyXploreCharacterModel characterModel1, ApphubWsClient memberClient) {
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
    }
}
