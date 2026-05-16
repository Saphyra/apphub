package com.github.saphyra.apphub.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatRoomCreatedMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SystemMessage;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyForbiddenOperation;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyNotTranslatedNotFound;
import static org.assertj.core.api.Assertions.assertThat;

public class LeaveChatRoomTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String ROOM_TITLE = "room-title";

    @Test(groups = {"be", "skyxplore"})
    public void leaveAllianceRoom() {
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

        Map<String, ApphubWsClient> gameWsClients = SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken1, userId1), new Player(accessToken2, userId2));

        leaveAllianceRoom(accessToken1);
        leaveGeneralRoom(accessToken1);
        chatRoomNotFound(accessToken1);
        leaveChatRoom(characterModel1, accessToken1, userId1, accessToken2, gameWsClients);
    }

    private static void leaveAllianceRoom(String accessToken1) {
        Response leaveAllianceRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessToken1, "alliance");
        verifyForbiddenOperation(leaveAllianceRoomResponse);
    }

    private static void leaveGeneralRoom(String accessToken1) {
        Response leaveGeneralRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessToken1, "general");
        verifyForbiddenOperation(leaveGeneralRoomResponse);
    }

    private static void chatRoomNotFound(String accessToken1) {
        Response chatRoomNotFoundResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessToken1, "unknown-chat-room");
        verifyNotTranslatedNotFound(chatRoomNotFoundResponse, 404);
    }

    private static void leaveChatRoom(SkyXploreCharacterModel characterModel1, String accessToken1, UUID userId1, String accessToken2, Map<String, ApphubWsClient> gameWsClients) {
        CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(List.of(userId1))
            .build();
        SkyXploreGameChatActions.createChatRoom(getServerPort(), accessToken2, createChatRoomRequest);
        String roomId = gameWsClients.get(accessToken2).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED)
            .map(event -> event.getPayloadAs(ChatRoomCreatedMessage.class))
            .map(ChatRoomCreatedMessage::getRoomId)
            .orElseThrow(() -> new RuntimeException("ChatRoom was not created"));
        Response leaveChatRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessToken1, roomId);

        assertThat(leaveChatRoomResponse.getStatusCode()).isEqualTo(200);

        SystemMessage message = gameWsClients.get(accessToken2).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)
            .map(event -> event.getPayloadAs(SystemMessage.class))
            .orElseThrow(() -> new RuntimeException("UserLeft message did not arrive"));

        assertThat(message.getUserId()).isEqualTo(userId1);
        assertThat(message.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(message.getRoom()).isEqualTo(roomId);
    }
}
