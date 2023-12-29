package com.github.saphyra.apphub.service.skyxplore.game.ws.main.service;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.CommonSkyXploreConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.SystemMessage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PlayerDisconnectedServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID CONNECTED_USER_ID = UUID.randomUUID();
    private static final String CHAT_ROOM_ID = "chat-room-id";
    private static final Integer ABANDONED_GAME_EXPIRATION_MINUTES = 342;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private GameDao gameDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CommonSkyXploreConfiguration configuration;

    @Mock
    private SkyXploreGameMainWebSocketHandler webSocketHandler;

    @InjectMocks
    private PlayerDisconnectedService underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private ChatRoom usedChatRoom;

    @Mock
    private ChatRoom unusedChatRoom;

    @Mock
    private Chat chat;

    @Test
    void playerDisconnected_gameNotFound() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        underTest.playerDisconnected(USER_ID, webSocketHandler);

        then(webSocketHandler).shouldHaveNoInteractions();
    }

    @Test
    void playerDisconnected_noMoreConnectedPlayers() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(game.getConnectedPlayers()).willReturn(Collections.emptyList());
        given(configuration.getAbandonedGameExpirationSeconds()).willReturn(ABANDONED_GAME_EXPIRATION_MINUTES);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.playerDisconnected(USER_ID, webSocketHandler);

        then(game).should().setExpiresAt(CURRENT_TIME.plusSeconds(ABANDONED_GAME_EXPIRATION_MINUTES));
        then(player).should().setConnected(false);
        then(webSocketHandler).shouldHaveNoInteractions();
    }

    @Test
    void playerDisconnected() {
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(game.getConnectedPlayers()).willReturn(List.of(CONNECTED_USER_ID));
        given(characterProxy.getCharacterName(USER_ID)).willReturn(CHARACTER_NAME);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(List.of(usedChatRoom, unusedChatRoom));
        given(usedChatRoom.getMembers()).willReturn(List.of(CONNECTED_USER_ID, USER_ID));
        given(game.filterConnectedPlayersFrom(List.of(CONNECTED_USER_ID, USER_ID))).willReturn(List.of(CONNECTED_USER_ID));
        given(usedChatRoom.getId()).willReturn(CHAT_ROOM_ID);

        underTest.playerDisconnected(USER_ID, webSocketHandler);

        then(game).should(times(0)).setExpiresAt(any());
        then(player).should().setConnected(false);
        then(game).should().setGamePaused(true);
        then(webSocketHandler).should().sendEvent(List.of(CONNECTED_USER_ID), WebSocketEventName.SKYXPLORE_GAME_PAUSED, true);
        then(webSocketHandler).should().sendEvent(List.of(CONNECTED_USER_ID), WebSocketEventName.SKYXPLORE_GAME_USER_LEFT, new SystemMessage(CHAT_ROOM_ID, CHARACTER_NAME, USER_ID));
    }
}
