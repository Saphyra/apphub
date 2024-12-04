package com.github.saphyra.apphub.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatRoomCreatedMessage;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatRoomResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyForbiddenOperation;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateChatRoomTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String ROOM_TITLE = "room-title";

    @Test(groups = {"be", "skyxplore"})
    public void createChatRoom() {
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

        nullMembers(accessTokenId1);
        membersContainsNull(accessTokenId1);
        memberNotInGame(accessTokenId1);
        nullRoomTitle(accessTokenId1);
        roomTitleTooShort(accessTokenId1);
        roomTitleTooLong(accessTokenId1);
        create(accessTokenId1, userId2, gameWsClients);
        getChetRooms(accessTokenId1);
    }

    private static void nullMembers(UUID accessTokenId1) {
        CreateChatRoomRequest nullMembersRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(null)
            .build();
        Response nullMembersResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, nullMembersRequest);
        verifyInvalidParam(nullMembersResponse, "members", "must not be null");
    }

    private static void membersContainsNull(UUID accessTokenId1) {
        CreateChatRoomRequest membersContainsNullRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(new UUID[]{null}))
            .build();
        Response membersContainsNullResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, membersContainsNullRequest);
        verifyInvalidParam(membersContainsNullResponse, "members", "must not contain null");
    }

    private static void memberNotInGame(UUID accessTokenId1) {
        CreateChatRoomRequest memberNotInGameRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(UUID.randomUUID()))
            .build();
        Response memberNotInGameResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, memberNotInGameRequest);
        verifyForbiddenOperation(memberNotInGameResponse);
    }

    private static void nullRoomTitle(UUID accessTokenId1) {
        CreateChatRoomRequest nullRoomTitleRequest = CreateChatRoomRequest.builder()
            .roomTitle(null)
            .members(Collections.emptyList())
            .build();
        Response nullRoomTitleResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, nullRoomTitleRequest);
        verifyInvalidParam(nullRoomTitleResponse, "roomTitle", "must not be null");
    }

    private static void roomTitleTooShort(UUID accessTokenId1) {
        CreateChatRoomRequest roomTitleTooShortRequest = CreateChatRoomRequest.builder()
            .roomTitle("a")
            .members(Collections.emptyList())
            .build();
        Response roomTitleTooShortResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, roomTitleTooShortRequest);
        verifyInvalidParam(roomTitleTooShortResponse, "roomTitle", "too short");
    }

    private static void roomTitleTooLong(UUID accessTokenId1) {
        CreateChatRoomRequest roomTitleTooLongRequest = CreateChatRoomRequest.builder()
            .roomTitle(Stream.generate(() -> "a").limit(21).collect(Collectors.joining()))
            .members(Collections.emptyList())
            .build();
        Response roomTitleTooLongResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, roomTitleTooLongRequest);
        verifyInvalidParam(roomTitleTooLongResponse, "roomTitle", "too long");
    }

    private static void create(UUID accessTokenId1, UUID userId2, Map<UUID, ApphubWsClient> gameWsClients) {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(userId2))
            .build();
        Response createResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId1, request);
        assertThat(createResponse.getStatusCode()).isEqualTo(200);
        gameWsClients.values()
            .stream()
            .map(skyXploreGameWsClient -> skyXploreGameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED))
            .map(webSocketEvent -> webSocketEvent.orElseThrow(() -> new RuntimeException("ChatRoomCreated message has not arrived")))
            .map(event -> event.getPayloadAs(ChatRoomCreatedMessage.class))
            .forEach(chatRoomCreatedMessage -> assertThat(chatRoomCreatedMessage.getRoomTitle()).isEqualTo(ROOM_TITLE));
    }

    private void getChetRooms(UUID accessTokenId) {
        List<ChatRoomResponse> chatRooms = SkyXploreGameChatActions.getChatRooms(getServerPort(), accessTokenId);

        assertThat(chatRooms).extracting(ChatRoomResponse::getRoomTitle).containsExactlyInAnyOrder(ROOM_TITLE, Constants.GENERAL_CHAT_ROOM_NAME, Constants.ALLIANCE_CHAT_ROOM_NAME);
    }
}
