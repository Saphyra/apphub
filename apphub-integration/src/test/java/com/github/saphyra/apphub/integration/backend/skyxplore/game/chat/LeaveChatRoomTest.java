package com.github.saphyra.apphub.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
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

import java.util.Arrays;
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
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        Map<UUID, ApphubWsClient> gameWsClients = SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessTokenId1, userId1), new Player(accessTokenId2, userId2));

        leaveAllianceRoom(accessTokenId1);
        leaveGeneralRoom(accessTokenId1);
        chatRoomNotFound(accessTokenId1);
        leaveChatRoom(characterModel1, accessTokenId1, userId1, accessTokenId2, gameWsClients);
    }

    private static void leaveAllianceRoom(UUID accessTokenId1) {
        Response leaveAllianceRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessTokenId1, "alliance");
        verifyForbiddenOperation(leaveAllianceRoomResponse);
    }

    private static void leaveGeneralRoom(UUID accessTokenId1) {
        Response leaveGeneralRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessTokenId1, "general");
        verifyForbiddenOperation(leaveGeneralRoomResponse);
    }

    private static void chatRoomNotFound(UUID accessTokenId1) {
        Response chatRoomNotFoundResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessTokenId1, "unknown-chat-room");
        verifyNotTranslatedNotFound(chatRoomNotFoundResponse, 404);
    }

    private static void leaveChatRoom(SkyXploreCharacterModel characterModel1, UUID accessTokenId1, UUID userId1, UUID accessTokenId2, Map<UUID, ApphubWsClient> gameWsClients) {
        CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(userId1))
            .build();
        SkyXploreGameChatActions.createChatRoom(getServerPort(), accessTokenId2, createChatRoomRequest);
        String roomId = gameWsClients.get(accessTokenId2).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED)
            .map(event -> event.getPayloadAs(ChatRoomCreatedMessage.class))
            .map(ChatRoomCreatedMessage::getRoomId)
            .orElseThrow(() -> new RuntimeException("ChatRoom was not created"));
        Response leaveChatRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessTokenId1, roomId);

        assertThat(leaveChatRoomResponse.getStatusCode()).isEqualTo(200);

        SystemMessage message = gameWsClients.get(accessTokenId2).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)
            .map(event -> event.getPayloadAs(SystemMessage.class))
            .orElseThrow(() -> new RuntimeException("UserLeft message did not arrive"));

        assertThat(message.getUserId()).isEqualTo(userId1);
        assertThat(message.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(message.getRoom()).isEqualTo(roomId);
    }
}
