package com.github.saphyra.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.IncomingChatWsMessageForGame;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.OutgoingChatWsMessageForGame;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SendMessageTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String MESSAGE = "message";

    @Test(groups = "skyxplore")
    public void createChatRoom() {
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

        Map<UUID, ApphubWsClient> gameWsClients = SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1), new Player(accessTokenId2, userId2));

        ApphubWsClient hostClient = gameWsClients.get(accessTokenId1);
        ApphubWsClient memberClient = gameWsClients.get(accessTokenId2);

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

        ApphubWsClient.cleanUpConnections();
    }
}
