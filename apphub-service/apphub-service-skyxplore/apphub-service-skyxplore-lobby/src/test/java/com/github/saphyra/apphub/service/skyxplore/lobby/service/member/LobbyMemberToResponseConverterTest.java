package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LobbyMemberToResponseConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final Long CREATED_AT = 343L;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private LobbyMemberToResponseConverter underTest;

    @Mock
    private Member member;

    @Test
    public void convert() {
        given(member.getUserId()).willReturn(USER_ID);
        given(member.getStatus()).willReturn(LobbyMemberStatus.NOT_READY);
        given(member.getAlliance()).willReturn(ALLIANCE_ID);
        given(characterProxy.getCharacter(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());
        given(dateTimeUtil.getCurrentTimeEpochMillis()).willReturn(CREATED_AT);

        LobbyMemberResponse result = underTest.convertMember(member);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getStatus()).isEqualTo(LobbyMemberStatus.NOT_READY);
        assertThat(result.getCharacterName()).isEqualTo(USERNAME);
        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
    }
}