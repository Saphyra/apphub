package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.test.common.TestConstants;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.test.common.TestConstants.DEFAULT_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class SkyXploreGameChatControllerImplTestIt_createChatRoom {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ROOM_TITLE = "room-title";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @MockBean
    private MessageSenderApiClient messageSenderClient;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private ObjectMapperWrapper objectMapperWrapper;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);

        Game game = Game.builder()
            .players(CollectionUtils.toMap(
                new BiWrapper<>(USER_ID_1, Player.builder().build()),
                new BiWrapper<>(PLAYER_ID, Player.builder().build())
            ))
            .chat(Chat.builder().rooms(new ArrayList<>()).build())
            .build();
        gameDao.put(game);
    }

    @After
    public void clear() {
        gameDao.deleteAll();
    }

    @Test
    public void nullMembers() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(null)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams()).containsEntry("members", "must not be null");
    }

    @Test
    public void membersContainsNull() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(new UUID[]{null}))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams()).containsEntry("members", "must not contain null");
    }

    @Test
    public void memberFromDifferentGame() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(UUID.randomUUID()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void nullRoomTitle() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(null)
            .members(Arrays.asList(PLAYER_ID))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams()).containsEntry("roomTitle", "must not be null");
    }

    @Test
    public void roomTitleTooShort() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle("a")
            .members(Arrays.asList(PLAYER_ID))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_TITLE_TOO_SHORT);
    }

    @Test
    public void roomTitleTooLong() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(Stream.generate(() -> "a").limit(21).collect(Collectors.joining()))
            .members(Arrays.asList(PLAYER_ID))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_TITLE_TOO_LONG);
    }

    @Test
    public void createChatRoom() {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .roomTitle(ROOM_TITLE)
            .members(Arrays.asList(PLAYER_ID))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderClient).sendMessage(eq(MessageGroup.SKYXPLORE_GAME), argumentCaptor.capture(), eq(DEFAULT_LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactlyInAnyOrder(PLAYER_ID, USER_ID_1);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_CHAT_ROOM_CREATED);

        Map<String, String> payload = objectMapperWrapper.convertValue(event.getPayload(), StringStringMap.class);
        assertThat(payload).containsKeys("id");
        assertThat(payload).containsEntry("title", ROOM_TITLE);
    }
}