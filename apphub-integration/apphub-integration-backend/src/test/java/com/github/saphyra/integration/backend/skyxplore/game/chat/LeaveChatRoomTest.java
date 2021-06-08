package com.github.saphyra.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ChatRoomCreatedMessage;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SystemMessage;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaveChatRoomTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String ROOM_TITLE = "room-title";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void leaveAllianceRoom(Language language) {
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

        //Leave alliance room
        Response leaveAllianceRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(language, accessTokenId1, "alliance");
        verifyForbiddenOperation(language, leaveAllianceRoomResponse);

        //Leave general room
        Response leaveGeneralRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(language, accessTokenId1, "general");
        verifyForbiddenOperation(language, leaveGeneralRoomResponse);

        //Chat room not found
        Response chatRoomNotFoundResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(language, accessTokenId1, "unknown-chat-room");
        verifyNotTranslatedNotFound(chatRoomNotFoundResponse);

        //Leave chat room
        CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(userId1))
            .build();
        SkyXploreGameChatActions.createChatRoom(language, accessTokenId2, createChatRoomRequest);
        String roomId = gameWsClients.get(accessTokenId2).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED)
            .map(event -> event.getPayloadAs(ChatRoomCreatedMessage.class))
            .map(ChatRoomCreatedMessage::getId)
            .orElseThrow(() -> new RuntimeException("ChatRoom was not created"));
        Response leaveChatRoomResponse = SkyXploreGameChatActions.getLeaveChatRoomResponse(language, accessTokenId1, roomId);

        assertThat(leaveChatRoomResponse.getStatusCode()).isEqualTo(200);

        SystemMessage message = gameWsClients.get(accessTokenId2).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)
            .map(event -> event.getPayloadAs(SystemMessage.class))
            .orElseThrow(() -> new RuntimeException("UserLeft message did not arrive"));

        assertThat(message.getUserId()).isEqualTo(userId1);
        assertThat(message.getCharacterName()).isEqualTo(characterModel1.getName());
        assertThat(message.getRoom()).isEqualTo(roomId);
    }

    private void verifyNotTranslatedNotFound(Response chatRoomNotFoundResponse) {
        assertThat(chatRoomNotFoundResponse.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = chatRoomNotFoundResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NON_TRANSLATED_ERROR.name());
    }

    private void verifyForbiddenOperation(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(403);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));
    }
}
