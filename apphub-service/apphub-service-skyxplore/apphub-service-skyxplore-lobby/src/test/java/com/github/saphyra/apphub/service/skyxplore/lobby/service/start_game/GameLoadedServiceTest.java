package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GameLoadedServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @InjectMocks
    private GameLoadedService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer connectedPlayer;

    @Mock
    private LobbyPlayer disconnectedPlayer;

    @Test
    void gameLoaded() {
        given(lobbyDao.findByGameId(GAME_ID)).willReturn(Optional.of(lobby));
        given(lobby.getPlayers()).willReturn(Map.of(
            UUID.randomUUID(), connectedPlayer,
            UUID.randomUUID(), disconnectedPlayer
        ));
        given(connectedPlayer.isConnected()).willReturn(true);
        given(connectedPlayer.getUserId()).willReturn(USER_ID);

        underTest.gameLoaded(GAME_ID);

        then(lobbyWebSocketHandler).should().sendEvent(List.of(USER_ID), WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);
        then(lobbyDao).should().delete(lobby);
    }
}