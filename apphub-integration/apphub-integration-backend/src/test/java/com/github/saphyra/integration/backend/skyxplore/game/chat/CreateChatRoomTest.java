package com.github.saphyra.integration.backend.skyxplore.game.chat;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
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
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateChatRoomTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";
    private static final String ROOM_TITLE = "room-title";

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void nullMembers(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1));

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(null)
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams()).containsEntry("members", "must not be null");
    }

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void membersContainsNull(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1));

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(new UUID[]{null}))
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams()).containsEntry("members", "must not contain null");
    }

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void memberNotInGame(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1));

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(UUID.randomUUID()))
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(403);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));
    }

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void nullRoomTitle(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1));

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(null)
            .members(Collections.emptyList())
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.INVALID_PARAM));
        assertThat(errorResponse.getParams()).containsEntry("roomTitle", "must not be null");
    }

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void roomTitleTooShort(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1));

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle("a")
            .members(Collections.emptyList())
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_TITLE_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHAT_ROOM_TITLE_TOO_SHORT));
    }

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void roomTitleTooLong(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1));

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(Stream.generate(() -> "a").limit(21).collect(Collectors.joining()))
            .members(Collections.emptyList())
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_TITLE_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.CHAT_ROOM_TITLE_TOO_LONG));
    }

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

        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(userId2))
            .build();

        Response response = SkyXploreGameChatActions.getCreateChatRoomResponse(language, accessTokenId1, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        gameWsClients
            .values()
            .stream()
            .map(skyXploreGameWsClient -> skyXploreGameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED))
            .map(webSocketEvent -> webSocketEvent.orElseThrow(() -> new RuntimeException("ChatRoomCreated message has not arrived")))
            .map(event -> event.getPayloadAs(ChatRoomCreatedMessage.class))
            .forEach(chatRoomCreatedMessage -> assertThat(chatRoomCreatedMessage.getTitle()).isEqualTo(ROOM_TITLE));
    }
}
