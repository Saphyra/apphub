package com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.feature.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.settings.alliance.AllianceService;
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
    private AccessToken accessToken;

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
        given(accessToken.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getAlliancesOfLobby() {
        given(allianceService.getAlliances(USER_ID)).willReturn(List.of(allianceResponse));

        List<AllianceResponse> result = underTest.getAlliancesOfLobby(accessToken);

        assertThat(result).containsExactly(allianceResponse);
    }

    @Test
    void editSettings() {
        underTest.editSettings(settings, accessToken);

        verify(editSettingsService).editSettings(USER_ID, settings);
    }

    @Test
    void getGameSettings() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getSettings()).willReturn(settings);

        SkyXploreGameSettings result = underTest.getGameSettings(accessToken);

        assertThat(result).isEqualTo(settings);
    }

    @Test
    void createOrModifyAi() {
        underTest.createOrModifyAi(aiPlayer, accessToken);

        verify(aiService).createOrModifyAi(USER_ID, aiPlayer);
    }

    @Test
    void removeAi() {
        underTest.removeAi(AI_USER_ID, accessToken);

        verify(aiService).removeAi(USER_ID, AI_USER_ID);
    }

    @Test
    void getAis() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getAis()).willReturn(List.of(aiPlayer));

        List<AiPlayer> result = underTest.getAis(accessToken);

        assertThat(result).containsExactly(aiPlayer);
    }
}