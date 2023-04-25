package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
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
class LobbyMemberQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private LobbyMemberToResponseConverter converter;

    @InjectMocks
    private LobbyMemberQueryService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyMember lobbyMember;

    @Mock
    private Invitation invitation;

    @Mock
    private LobbyMemberResponse memberResponse;

    @Mock
    private LobbyMemberResponse invitationResponse;

    @Test
    void getMembers() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getMembers()).willReturn(Map.of(UUID.randomUUID(), lobbyMember));
        given(converter.convertMember(lobbyMember)).willReturn(memberResponse);
        given(lobby.getInvitations()).willReturn(List.of(invitation));
        given(converter.convertInvitation(invitation)).willReturn(invitationResponse);

        List<LobbyMemberResponse> result = underTest.getMembers(USER_ID);

        assertThat(result).containsExactlyInAnyOrder(memberResponse, invitationResponse);
    }
}