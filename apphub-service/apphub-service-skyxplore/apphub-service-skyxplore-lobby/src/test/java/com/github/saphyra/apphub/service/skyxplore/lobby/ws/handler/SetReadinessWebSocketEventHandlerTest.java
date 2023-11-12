package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.player.LobbyPlayerToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SetReadinessWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private LobbyPlayerToResponseConverter lobbyPlayerToResponseConverter;

    @InjectMocks
    private SetReadinessWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Mock
    private LobbyPlayerResponse lobbyPlayerResponse;

    @Test
    public void canHandle_setReadinessEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void setReadiness() {
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        Map<UUID, LobbyPlayer> players = CollectionUtils.singleValueMap(FROM, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        given(lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer)).willReturn(lobbyPlayerResponse);

        underTest.handle(FROM, WebSocketEvent.builder().payload(String.valueOf(true)).build(), lobbyWebSocketHandler);

        verify(lobbyPlayer).setStatus(LobbyPlayerStatus.READY);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyPlayerResponse);
    }
}