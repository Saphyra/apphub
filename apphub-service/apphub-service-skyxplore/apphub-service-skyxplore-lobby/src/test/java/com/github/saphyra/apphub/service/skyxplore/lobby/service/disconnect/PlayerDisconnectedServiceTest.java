package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
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
    private LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @InjectMocks
    private PlayerDisconnectedService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyMember player;

    @Mock
    private LobbyMemberResponse lobbyMemberResponse;

    @Test
    void playerDisconnected() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        Map<UUID, LobbyMember> players = Map.of(USER_ID, player);
        given(lobby.getMembers()).willReturn(players);
        given(lobbyMemberToResponseConverter.convertMember(player)).willReturn(lobbyMemberResponse);

        underTest.playerDisconnected(USER_ID);

        then(player).should().setStatus(LobbyMemberStatus.DISCONNECTED);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyMemberResponse);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED, lobbyMemberResponse);
    }
}