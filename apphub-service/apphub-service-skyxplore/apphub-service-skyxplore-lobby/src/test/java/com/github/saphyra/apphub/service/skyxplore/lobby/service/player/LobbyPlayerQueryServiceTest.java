package com.github.saphyra.apphub.service.skyxplore.lobby.service.player;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LobbyPlayerQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private LobbyPlayerToResponseConverter converter;

    @InjectMocks
    private LobbyPlayerQueryService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Mock
    private Invitation invitation;

    @Mock
    private LobbyPlayerResponse playerResponse;

    @Mock
    private LobbyPlayerResponse invitationResponse;

    @Test
    void getPlayers() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getPlayers()).willReturn(Map.of(UUID.randomUUID(), lobbyPlayer));
        given(converter.convertPlayer(lobbyPlayer)).willReturn(playerResponse);
        given(lobby.getInvitations()).willReturn(List.of(invitation));
        given(converter.convertInvitation(invitation)).willReturn(invitationResponse);

        List<LobbyPlayerResponse> result = underTest.getPlayers(USER_ID);

        assertThat(result).containsExactlyInAnyOrder(playerResponse, invitationResponse);
    }
}