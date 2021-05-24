package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LobbyMemberQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID HOST_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private LobbyMemberResponseConverter converter;

    @InjectMocks
    private LobbyMemberQueryService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Member member;

    @Mock
    private Member hostMember;

    @Mock
    private LobbyMemberResponse lobbyMemberResponse;

    @Mock
    private LobbyMemberResponse hostMemberResponse;

    @Mock
    private Alliance alliance;

    @Test
    public void getMembers() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(
            new BiWrapper<>(USER_ID, member),
            new BiWrapper<>(HOST_ID, hostMember)
        ));
        given(converter.convertMember(member, Arrays.asList(alliance))).willReturn(lobbyMemberResponse);
        given(converter.convertMember(hostMember, Arrays.asList(alliance))).willReturn(hostMemberResponse);
        given(lobby.getHost()).willReturn(HOST_ID);
        given(member.getUserId()).willReturn(USER_ID);
        given(hostMember.getUserId()).willReturn(HOST_ID);
        given(lobby.getAlliances()).willReturn(Arrays.asList(alliance));

        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);

        LobbyMembersResponse result = underTest.getMembers(USER_ID);

        assertThat(result.getAlliances()).containsExactly(ALLIANCE_NAME);
        assertThat(result.getMembers()).containsExactly(lobbyMemberResponse);
        assertThat(result.getHost()).isEqualTo(hostMemberResponse);
    }
}