package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith({MockitoExtension.class})
class PlayerDisconnectedServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyPlayerToResponseConverter lobbyPlayerToResponseConverter;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @InjectMocks
    private PlayerDisconnectedService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer player;

    @Mock
    private LobbyPlayerResponse lobbyPlayerResponse;

    @Test
    void playerDisconnected() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        Map<UUID, LobbyPlayer> players = Map.of(USER_ID, player);
        given(lobby.getPlayers()).willReturn(players);
        given(lobbyPlayerToResponseConverter.convertPlayer(player)).willReturn(lobbyPlayerResponse);

        underTest.playerDisconnected(USER_ID);

        then(player).should().setStatus(LobbyPlayerStatus.DISCONNECTED);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyPlayerResponse);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED, lobbyPlayerResponse);
    }
}