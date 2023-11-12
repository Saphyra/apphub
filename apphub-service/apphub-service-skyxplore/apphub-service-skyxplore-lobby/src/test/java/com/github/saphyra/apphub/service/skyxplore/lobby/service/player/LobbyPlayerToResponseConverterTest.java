package com.github.saphyra.apphub.service.skyxplore.lobby.service.player;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
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
public class LobbyPlayerToResponseConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final Long CREATED_AT = 343L;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private LobbyPlayerToResponseConverter underTest;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Mock
    private Invitation invitation;

    @Test
    public void convertPlayer() {
        given(lobbyPlayer.getUserId()).willReturn(USER_ID);
        given(lobbyPlayer.getStatus()).willReturn(LobbyPlayerStatus.NOT_READY);
        given(lobbyPlayer.getAllianceId()).willReturn(ALLIANCE_ID);
        given(characterProxy.getCharacter(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());
        given(dateTimeUtil.getCurrentTimeEpochMillis()).willReturn(CREATED_AT);

        LobbyPlayerResponse result = underTest.convertPlayer(lobbyPlayer);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getStatus()).isEqualTo(LobbyPlayerStatus.NOT_READY);
        assertThat(result.getCharacterName()).isEqualTo(USERNAME);
        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void convertInvitation() {
        given(invitation.getCharacterId()).willReturn(USER_ID);
        given(characterProxy.getCharacter(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(USERNAME).build());

        LobbyPlayerResponse result = underTest.convertInvitation(invitation);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getCharacterName()).isEqualTo(USERNAME);
        assertThat(result.getStatus()).isEqualTo(LobbyPlayerStatus.INVITED);
    }
}