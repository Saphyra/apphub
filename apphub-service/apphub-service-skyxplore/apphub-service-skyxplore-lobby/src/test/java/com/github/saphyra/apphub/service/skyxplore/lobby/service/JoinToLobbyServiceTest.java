package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.player.LobbyPlayerToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JoinToLobbyServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID INVITOR_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private LobbyPlayerToResponseConverter lobbyPlayerToResponseConverter;

    @InjectMocks
    private JoinToLobbyService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Invitation invitation;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Mock
    private LobbyPlayerResponse lobbyPlayerResponse;

    @Test
    void acceptInvitation_notInvited() {
        given(lobbyDao.findByUserIdValidated(INVITOR_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(List.of(invitation));
        given(invitation.getCharacterId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.acceptInvitation(USER_ID, INVITOR_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    void acceptInvitation() {
        given(lobbyDao.findByUserIdValidated(INVITOR_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(invitation));
        given(invitation.getCharacterId()).willReturn(USER_ID);
        Map<UUID, LobbyPlayer> players = new HashMap<>();
        given(lobby.getPlayers()).willReturn(players);

        underTest.acceptInvitation(USER_ID, INVITOR_ID);

        assertThat(lobby.getInvitations()).isEmpty();
        assertThat(players).hasSize(1);
        assertThat(players.get(USER_ID).getUserId()).isEqualTo(USER_ID);
        assertThat(players.get(USER_ID).getStatus()).isEqualTo(LobbyPlayerStatus.DISCONNECTED);
    }

    @Test
    void userJoinedToLobby() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        Map<UUID, LobbyPlayer> players = Map.of(USER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        given(lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer)).willReturn(lobbyPlayerResponse);

        underTest.userJoinedToLobby(USER_ID);

        verify(lobbyPlayer).setConnected(true);
        verify(lobbyPlayer).setStatus(LobbyPlayerStatus.NOT_READY);

        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyPlayerResponse);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED, lobbyPlayerResponse);
    }
}