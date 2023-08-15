package com.github.saphyra.apphub.integraton.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.ChatRoomCreatedMessage;
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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void createChatRoom(Language language) {
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

        nullMembers(language, accessTokenId1);
        membersContainsNull(language, accessTokenId1);
        memberNotInGame(language, accessTokenId1);
        nullRoomTitle(language, accessTokenId1);
        roomTitleTooShort(language, accessTokenId1);
        roomTitleTooLong(language, accessTokenId1);
        create(language, accessTokenId1, userId2, gameWsClients);
    }

    private static void nullMembers(Language language, UUID accessTokenId1) {
        CreateChatRoomRequest nullMembersRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(null)
            .build();
        Response nullMembersResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, nullMembersRequest);
        verifyInvalidParam(language, nullMembersResponse, "members", "must not be null");
    }

    private static void membersContainsNull(Language language, UUID accessTokenId1) {
        CreateChatRoomRequest membersContainsNullRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(new UUID[]{null}))
            .build();
        Response membersContainsNullResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, membersContainsNullRequest);
        verifyInvalidParam(language, membersContainsNullResponse, "members", "must not contain null");
    }

    private static void memberNotInGame(Language language, UUID accessTokenId1) {
        CreateChatRoomRequest memberNotInGameRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(UUID.randomUUID()))
            .build();
        Response memberNotInGameResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, memberNotInGameRequest);
        verifyForbiddenOperation(language, memberNotInGameResponse);
    }

    private static void nullRoomTitle(Language language, UUID accessTokenId1) {
        CreateChatRoomRequest nullRoomTitleRequest = CreateChatRoomRequest.builder()
            .roomTitle(null)
            .members(Collections.emptyList())
            .build();
        Response nullRoomTitleResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, nullRoomTitleRequest);
        verifyInvalidParam(language, nullRoomTitleResponse, "roomTitle", "must not be null");
    }

    private static void roomTitleTooShort(Language language, UUID accessTokenId1) {
        CreateChatRoomRequest roomTitleTooShortRequest = CreateChatRoomRequest.builder()
            .roomTitle("a")
            .members(Collections.emptyList())
            .build();
        Response roomTitleTooShortResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, roomTitleTooShortRequest);
        verifyInvalidParam(language, roomTitleTooShortResponse, "roomTitle", "too short");
    }

    private static void roomTitleTooLong(Language language, UUID accessTokenId1) {
        CreateChatRoomRequest roomTitleTooLongRequest = CreateChatRoomRequest.builder()
            .roomTitle(Stream.generate(() -> "a").limit(21).collect(Collectors.joining()))
            .members(Collections.emptyList())
            .build();
        Response roomTitleTooLongResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, roomTitleTooLongRequest);
        verifyInvalidParam(language, roomTitleTooLongResponse, "roomTitle", "too long");
    }

    private static void create(Language language, UUID accessTokenId1, UUID userId2, Map<UUID, ApphubWsClient> gameWsClients) {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(userId2))
            .build();
        Response createResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);
        assertThat(createResponse.getStatusCode()).isEqualTo(200);
        gameWsClients.values()
            .stream()
            .map(skyXploreGameWsClient -> skyXploreGameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED))
            .map(webSocketEvent -> webSocketEvent.orElseThrow(() -> new RuntimeException("ChatRoomCreated message has not arrived")))
            .map(event -> event.getPayloadAs(ChatRoomCreatedMessage.class))
            .forEach(chatRoomCreatedMessage -> assertThat(chatRoomCreatedMessage.getTitle()).isEqualTo(ROOM_TITLE));
    }
}
