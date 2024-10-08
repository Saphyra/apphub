package com.github.saphyra.apphub.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.IncomingChatWsMessageForGame;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.OutgoingChatWsMessageForGame;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SendMessageTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String MESSAGE = "message";

    @Test(groups = {"be", "skyxplore"})
    public void sendMessage() {
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

        Map<UUID, ApphubWsClient> gameWsClients = SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessTokenId1, userId1), new Player(accessTokenId2, userId2));

        ApphubWsClient hostClient = gameWsClients.get(accessTokenId1);
        ApphubWsClient memberClient = gameWsClients.get(accessTokenId2);

        messageTooLong(hostClient, memberClient);

        sendMessage(hostClient, userId1, characterModel1, memberClient);
    }

    private void messageTooLong(ApphubWsClient hostClient, ApphubWsClient memberClient) {
        IncomingChatWsMessageForGame message = new IncomingChatWsMessageForGame(
            Constants.GENERAL_CHAT_ROOM_NAME,
            Stream.generate(() -> "a").limit(1025).collect(Collectors.joining())
        );
        WebSocketEvent sentEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE)
            .payload(message)
            .build();
        hostClient.send(sentEvent);

        assertThat(hostClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE)).isEmpty();
        assertThat(memberClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE)).isEmpty();
    }

    private static void sendMessage(ApphubWsClient hostClient, UUID userId1, SkyXploreCharacterModel characterModel1, ApphubWsClient memberClient) {
        IncomingChatWsMessageForGame message = new IncomingChatWsMessageForGame(Constants.GENERAL_CHAT_ROOM_NAME, MESSAGE);
        WebSocketEvent sentEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE)
            .payload(message)
            .build();
        hostClient.send(sentEvent);

        OutgoingChatWsMessageForGame expectedMessage = OutgoingChatWsMessageForGame.builder()
            .room(Constants.GENERAL_CHAT_ROOM_NAME)
            .message(MESSAGE)
            .senderId(userId1)
            .senderName(characterModel1.getName())
            .build();
        assertThat(hostClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE).map(event -> event.getPayloadAs(OutgoingChatWsMessageForGame.class))).contains(expectedMessage);
        assertThat(memberClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_SEND_MESSAGE).map(event -> event.getPayloadAs(OutgoingChatWsMessageForGame.class))).contains(expectedMessage);
    }
}
