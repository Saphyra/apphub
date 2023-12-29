package com.github.saphyra.apphub.service.skyxplore.game.ws.main.service;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PlayerConnectedServiceTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final String CHAT_ROOM_ID = "chat-room-id";

    @Mock
    private GameDao gameDao;

    @Mock
    private CharacterProxy characterProxy;

    @InjectMocks
    private PlayerConnectedService underTest;

    @Mock
    private SkyXploreGameMainWebSocketHandler webSocketHandler;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private Chat chat;

    @Mock
    private ChatRoom chatRoom;

    @Test
    void playerConnected(){
        given(gameDao.findByUserIdValidated(USER_ID_1)).willReturn(game);
        given(game.getPlayers()).willReturn(Map.of(USER_ID_1, player));
        given(game.isGamePaused()).willReturn(true);
        given(characterProxy.getCharacterName(USER_ID_1)).willReturn(CHARACTER_NAME);
        given(game.getChat()).willReturn(chat);
        given(chat.getRooms()).willReturn(List.of(chatRoom));
        given(chatRoom.getMembers()).willReturn(List.of(USER_ID_1, USER_ID_2));
        given(game.filterConnectedPlayersFrom(List.of(USER_ID_1, USER_ID_2))).willReturn(List.of(USER_ID_2));
        given(chatRoom.getId()).willReturn(CHAT_ROOM_ID);

        underTest.playerConnected(USER_ID_1, webSocketHandler);

        then(player).should().setConnected(true);
        then(webSocketHandler).should().sendEvent(USER_ID_1, WebSocketEventName.SKYXPLORE_GAME_PAUSED, true);
        then(webSocketHandler).should().sendEvent(List.of(USER_ID_2), WebSocketEventName.SKYXPLORE_GAME_USER_JOINED, new SystemMessage(CHAT_ROOM_ID, CHARACTER_NAME, USER_ID_1));
    }
}