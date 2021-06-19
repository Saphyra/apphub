package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
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
import java.util.UUID;

import static com.github.saphyra.apphub.test.common.TestConstants.DEFAULT_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class SkyXploreGameChatControllerImplTestIt_leaveChatRoom {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String USERNAME = "username";
    private static final String ROOM_TITLE = "room-title";
    private static final String CHAT_ROOM_ID = "chat-room-id";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @MockBean
    private MessageSenderApiClient messageSenderClient;

    @MockBean
    private SkyXploreCharacterDataApiClient characterClient;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    Chat chat = Chat.builder()
        .rooms(new ArrayList<>())
        .build();

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);

        Game game = Game.builder()
            .players(CollectionUtils.toMap(
                new BiWrapper<>(USER_ID_1, Player.builder().build()),
                new BiWrapper<>(PLAYER_ID, Player.builder().build())
            ))
            .chat(chat)
            .build();
        gameDao.put(game);
    }

    @After
    public void clear() {
        gameDao.deleteAll();
    }

    @Test
    public void leaveGeneralChat() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", GameConstants.CHAT_ROOM_GENERAL));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void leaveAllianceChat() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", GameConstants.CHAT_ROOM_ALLIANCE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void chatRoomNotFound() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", UUID.randomUUID()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void notMemberOfChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
            .id(CHAT_ROOM_ID)
            .members(Arrays.asList(UUID.randomUUID()))
            .build();
        chat.addRoom(chatRoom);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", CHAT_ROOM_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(chat.getRooms()).containsExactly(chatRoom);
        verifyNoInteractions(messageSenderClient);
    }

    @Test
    public void noMoreMembers() {
        ChatRoom chatRoom = ChatRoom.builder()
            .id(CHAT_ROOM_ID)
            .members(CollectionUtils.toList(USER_ID_1))
            .build();
        chat.addRoom(chatRoom);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", CHAT_ROOM_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(chat.getRooms()).isEmpty();
        verifyNoInteractions(messageSenderClient);
    }

    @Test
    public void leaveChatRoom() {
        given(characterClient.internalGetCharacterByUserId(USER_ID_1, DEFAULT_LOCALE)).willReturn(new SkyXploreCharacterModel(USER_ID_1, USERNAME));

        ChatRoom chatRoom = ChatRoom.builder()
            .id(CHAT_ROOM_ID)
            .members(CollectionUtils.toList(USER_ID_1, PLAYER_ID))
            .build();
        chat.addRoom(chatRoom);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM, "roomId", CHAT_ROOM_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(chatRoom.getMembers()).containsExactly(PLAYER_ID);
        assertThat(chat.getRooms()).containsExactly(chatRoom);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderClient).sendMessage(eq(MessageGroup.SKYXPLORE_GAME), argumentCaptor.capture(), eq(DEFAULT_LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactlyInAnyOrder(PLAYER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT);

        SystemMessage payload = (SystemMessage) event.getPayload();
        assertThat(payload.getRoom()).isEqualTo(CHAT_ROOM_ID);
        assertThat(payload.getCharacterName()).isEqualTo(USERNAME);
        assertThat(payload.getUserId()).isEqualTo(USER_ID_1);
    }
}