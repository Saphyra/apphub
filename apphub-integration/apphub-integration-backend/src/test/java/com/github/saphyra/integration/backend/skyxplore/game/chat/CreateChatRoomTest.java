package com.github.saphyra.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ChatRoomCreatedMessage;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyForbiddenOperation;
import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateChatRoomTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String ROOM_TITLE = "room-title";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
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

        //Null members
        CreateChatRoomRequest nullMembersRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(null)
            .build();
        Response nullMembersResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, nullMembersRequest);
        verifyInvalidParam(language, nullMembersResponse, "members", "must not be null");

        //Members contains null
        CreateChatRoomRequest membersContainsNullRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(new UUID[]{null}))
            .build();
        Response membersContainsNullResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, membersContainsNullRequest);
        verifyInvalidParam(language, membersContainsNullResponse, "members", "must not contain null");

        //Member not in game
        CreateChatRoomRequest memberNotInGameRequest = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(UUID.randomUUID()))
            .build();
        Response memberNotInGameResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, memberNotInGameRequest);
        verifyForbiddenOperation(language, memberNotInGameResponse);

        //Null room title
        CreateChatRoomRequest nullRoomTitleRequest = CreateChatRoomRequest.builder()
            .roomTitle(null)
            .members(Collections.emptyList())
            .build();
        Response nullRoomTitleResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, nullRoomTitleRequest);
        verifyInvalidParam(language, nullRoomTitleResponse, "roomTitle", "must not be null");

        //Room title too short
        CreateChatRoomRequest roomTitleTooShortRequest = CreateChatRoomRequest.builder()
            .roomTitle("a")
            .members(Collections.emptyList())
            .build();
        Response roomTitleTooShortResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, roomTitleTooShortRequest);
        verifyInvalidParam(language, roomTitleTooShortResponse, "roomTitle", "too short");

        //Room title too long
        CreateChatRoomRequest roomTitleTooLongRequest = CreateChatRoomRequest.builder()
            .roomTitle(Stream.generate(() -> "a").limit(21).collect(Collectors.joining()))
            .members(Collections.emptyList())
            .build();
        Response roomTitleTooLongResponse = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, roomTitleTooLongRequest);
        verifyInvalidParam(language, roomTitleTooLongResponse, "roomTitle", "too long");

        //Create
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
