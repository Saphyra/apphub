package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ExitFromLobbyServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private InvitationRejectionService invitationRejectionService;

    @Mock
    private ExitMessageSender exitMessageSender;

    @InjectMocks
    private ExitFromLobbyService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer player;

    @Test
    void exit_notHost() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(lobby));
        given(lobby.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(lobby.getHost()).willReturn(UUID.randomUUID());

        underTest.exit(USER_ID);

        assertThat(lobby.getPlayers()).isEmpty();
        then(invitationRejectionService).should().rejectInvitations(USER_ID, lobby);
        then(exitMessageSender).should().sendExitMessage(USER_ID, lobby, false);
        then(lobbyDao).should(times(0)).delete(any());
    }

    @Test
    void exit_host() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(lobby));
        given(lobby.getPlayers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(lobby.getHost()).willReturn(USER_ID);

        underTest.exit(USER_ID);

        assertThat(lobby.getPlayers()).isEmpty();
        then(invitationRejectionService).should().rejectInvitations(USER_ID, lobby);
        then(exitMessageSender).should().sendExitMessage(USER_ID, lobby, false);
        then(lobbyDao).should().delete(lobby);
    }
}