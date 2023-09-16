package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
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
    private LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    @InjectMocks
    private JoinToLobbyService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Invitation invitation;

    @Mock
    private LobbyMember lobbyMember;

    @Mock
    private LobbyMemberResponse lobbyMemberResponse;

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
        Map<UUID, LobbyMember> members = new HashMap<>();
        given(lobby.getMembers()).willReturn(members);

        underTest.acceptInvitation(USER_ID, INVITOR_ID);

        assertThat(lobby.getInvitations()).isEmpty();
        assertThat(members).hasSize(1);
        assertThat(members.get(USER_ID).getUserId()).isEqualTo(USER_ID);
        assertThat(members.get(USER_ID).getStatus()).isEqualTo(LobbyMemberStatus.DISCONNECTED);
    }

    @Test
    void userJoinedToLobby() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        Map<UUID, LobbyMember> members = Map.of(USER_ID, lobbyMember);
        given(lobby.getMembers()).willReturn(members);
        given(lobbyMemberToResponseConverter.convertMember(lobbyMember)).willReturn(lobbyMemberResponse);

        underTest.userJoinedToLobby(USER_ID);

        verify(lobbyMember).setConnected(true);
        verify(lobbyMember).setStatus(LobbyMemberStatus.NOT_READY);

        then(lobbyWebSocketHandler).should().sendEvent(members.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyMemberResponse);
        then(lobbyWebSocketHandler).should().sendEvent(members.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED, lobbyMemberResponse);
    }
}