package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
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
public class LobbyMemberResponseConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final String USERNAME = "username";

    @Mock
    private CharacterProxy characterProxy;

    @InjectMocks
    private LobbyMemberResponseConverter underTest;

    @Mock
    private Member member;

    @Mock
    private Alliance alliance;

    @Test
    public void convert() {
        given(member.getUserId()).willReturn(USER_ID);
        given(member.isReady()).willReturn(true);
        given(member.getAlliance()).willReturn(ALLIANCE_ID);
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(characterProxy.getCharacter(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());

        LobbyMemberResponse result = underTest.convertMember(member, Arrays.asList(alliance));

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.isReady()).isTrue();
        assertThat(result.getCharacterName()).isEqualTo(USERNAME);
        assertThat(result.getAlliance()).isEqualTo(ALLIANCE_NAME);
    }
}