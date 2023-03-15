package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.CommonSkyXploreConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameWebSocketEventControllerImplTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String ROOM_ID = "room-id";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final Integer ABANDONED_GAME_EXPIRATION_SECONDS = 34;

    @Mock
    private GameDao gameDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private WebSocketEventHandler webSocketEventHandler;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CommonSkyXploreConfiguration configuration;

    @Mock
    private WebSocketMessageFactory webSocketMessageFactory;

    private SkyXploreGameWebSocketEventControllerImpl underTest;

    @Mock
    private WebSocketEvent webSocketEvent;

    @Mock
    private Game game;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;

    @Mock
    private ChatRoom otherChatRoom;

    @Mock
    private WebSocketMessage gamePausedMessage;

    @Mock
    private WebSocketMessage chatMessage;

    @BeforeEach
    public void setUp() {
        underTest = SkyXploreGameWebSocketEventControllerImpl.builder()
            .gameDao(gameDao)
            .characterProxy(characterProxy)
            .handlers(Arrays.asList(webSocketEventHandler))
            .messageSenderProxy(messageSenderProxy)
            .dateTimeUtil(dateTimeUtil)
            .configuration(configuration)
            .webSocketMessageFactory(webSocketMessageFactory)
            .build();
    }

    @Test
    public void processWebSocketEvent() {
        given(webSocketEvent.getEventName()).willReturn(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS);
        given(webSocketEventHandler.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)).willReturn(true);

        underTest.processWebSocketEvent(FROM, webSocketEvent);

        verify(webSocketEventHandler).handle(FROM, webSocketEvent);
    }

    @Test
    public void userJoinedToGame() {
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(Arrays.asList(chatRoom, otherChatRoom));
        given(chatRoom.getMembers()).willReturn(Arrays.asList(USER_ID, FROM));
        given(chatRoom.getId()).willReturn(ROOM_ID);
        given(otherChatRoom.getMembers()).willReturn(Collections.emptyList());
        given(game.filterConnectedPlayersFrom(any())).willReturn(Arrays.asList(USER_ID));
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player1));
        given(game.isGamePaused()).willReturn(true);

        given(webSocketMessageFactory.create(USER_ID, WebSocketEventName.SKYXPLORE_GAME_PAUSED, true)).willReturn(gamePausedMessage);
        given(webSocketMessageFactory.create(List.of(USER_ID), WebSocketEventName.SKYXPLORE_GAME_USER_JOINED, new SystemMessage(ROOM_ID, USERNAME, USER_ID))).willReturn(chatMessage);

        underTest.userJoinedToGame(USER_ID);

        verify(player1).setConnected(true);

        verify(messageSenderProxy).sendToGame(gamePausedMessage);
        verify(messageSenderProxy).sendToGame(chatMessage);
        verify(game).setExpiresAt(null);
    }

    @Test
    public void userLeftGame_connectedMemberLeft() {
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(Arrays.asList(chatRoom, otherChatRoom));
        given(chatRoom.getMembers()).willReturn(Arrays.asList(USER_ID, FROM));
        given(chatRoom.getId()).willReturn(ROOM_ID);
        given(otherChatRoom.getMembers()).willReturn(Collections.emptyList());
        given(game.filterConnectedPlayersFrom(any())).willReturn(Arrays.asList(USER_ID));
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getPlayers()).willReturn(CollectionUtils.toMap(new BiWrapper<>(USER_ID, player1), new BiWrapper<>(FROM, player2)));
        given(game.getConnectedPlayers()).willReturn(Arrays.asList(UUID.randomUUID()));
        given(game.isGamePaused()).willReturn(true);

        given(webSocketMessageFactory.create(List.of(USER_ID), WebSocketEventName.SKYXPLORE_GAME_PAUSED, true)).willReturn(gamePausedMessage);
        given(webSocketMessageFactory.create(List.of(USER_ID), WebSocketEventName.SKYXPLORE_GAME_USER_LEFT, new SystemMessage(ROOM_ID, USERNAME, USER_ID))).willReturn(chatMessage);

        underTest.userLeftGame(USER_ID);

        verify(player1).setConnected(false);
        verify(game).setGamePaused(true);

        verify(messageSenderProxy).sendToGame(gamePausedMessage);
        verify(messageSenderProxy).sendToGame(chatMessage);
    }

    @Test
    public void userLeftGame_noMoreConnectedMembers() {
        given(characterProxy.getCharacterByUserId(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getPlayers()).willReturn(CollectionUtils.toMap(new BiWrapper<>(USER_ID, player1)));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(configuration.getAbandonedGameExpirationSeconds()).willReturn(ABANDONED_GAME_EXPIRATION_SECONDS);

        underTest.userLeftGame(USER_ID);

        verify(player1).setConnected(false);
        verify(game).setExpiresAt(CURRENT_DATE.plusSeconds(ABANDONED_GAME_EXPIRATION_SECONDS));

        verifyNoInteractions(messageSenderProxy);
    }

}