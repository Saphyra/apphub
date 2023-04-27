package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance.AllianceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkyXploreLobbySettingsControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID AI_USER_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private EditSettingsService editSettingsService;

    @Mock
    private AiService aiService;

    @Mock
    private AllianceService allianceService;

    @InjectMocks
    private SkyXploreLobbySettingsControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SkyXploreGameSettings settings;

    @Mock
    private Lobby lobby;

    @Mock
    private AiPlayer aiPlayer;

    @Mock
    private AllianceResponse allianceResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getAlliancesOfLobby() {
        given(allianceService.getAlliances(USER_ID)).willReturn(List.of(allianceResponse));

        List<AllianceResponse> result = underTest.getAlliancesOfLobby(accessTokenHeader);

        assertThat(result).containsExactly(allianceResponse);
    }

    @Test
    void editSettings() {
        underTest.editSettings(settings, accessTokenHeader);

        verify(editSettingsService).editSettings(USER_ID, settings);
    }

    @Test
    void getGameSettings() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getSettings()).willReturn(settings);

        SkyXploreGameSettings result = underTest.getGameSettings(accessTokenHeader);

        assertThat(result).isEqualTo(settings);
    }

    @Test
    void createOrModifyAi() {
        underTest.createOrModifyAi(aiPlayer, accessTokenHeader);

        verify(aiService).createOrModifyAi(USER_ID, aiPlayer);
    }

    @Test
    void removeAi() {
        underTest.removeAi(AI_USER_ID, accessTokenHeader);

        verify(aiService).removeAi(USER_ID, AI_USER_ID);
    }

    @Test
    void getAis() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getAis()).willReturn(List.of(aiPlayer));

        List<AiPlayer> result = underTest.getAis(accessTokenHeader);

        assertThat(result).containsExactly(aiPlayer);
    }
}