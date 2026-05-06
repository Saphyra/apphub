package com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.feature.skyxplore.lobby.server.SkyXploreLobbySettingsController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.feature.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.settings.alliance.AllianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreLobbySettingsControllerImpl implements SkyXploreLobbySettingsController {
    private final LobbyDao lobbyDao;
    private final EditSettingsService editSettingsService;
    private final AiService aiService;
    private final AllianceService allianceService;

    @Override
    public void editSettings(SkyXploreGameSettings settings, AccessToken accessToken) {
        editSettingsService.editSettings(accessToken.getUserId(), settings);
    }

    @Override
    public SkyXploreGameSettings getGameSettings(AccessToken accessToken) {
        return lobbyDao.findByUserIdValidated(accessToken.getUserId())
            .getSettings();
    }

    @Override
    public void createOrModifyAi(AiPlayer aiPlayer, AccessToken accessToken) {
        aiService.createOrModifyAi(accessToken.getUserId(), aiPlayer);
    }

    @Override
    public void removeAi(UUID aiUserId, AccessToken accessToken) {
        aiService.removeAi(accessToken.getUserId(), aiUserId);
    }

    @Override
    public List<AiPlayer> getAis(AccessToken accessToken) {
        return lobbyDao.findByUserIdValidated(accessToken.getUserId())
            .getAis();
    }

    @Override
    public List<AllianceResponse> getAlliancesOfLobby(AccessToken accessToken) {
        log.info("{} wants to know the alliances of his lobby.", accessToken.getUserId());
        return allianceService.getAlliances(accessToken.getUserId());
    }

    @Override
    public void changeAllianceOfPlayer(OneParamRequest<String> alliance, UUID userId, AccessToken accessToken) {
        log.info("{} wants to change the alliance of player {} to {}", accessToken.getUserId(), userId, alliance.getValue());
        allianceService.setAllianceOfPlayer(accessToken.getUserId(), userId, alliance.getValue());
    }

    @Override
    public void changeAllianceOfAi(OneParamRequest<String> alliance, UUID userId, AccessToken accessToken) {
        log.info("{} wants to change the alliance of ai {} to {}", accessToken.getUserId(), userId, alliance.getValue());
        allianceService.setAllianceOfAi(accessToken.getUserId(), userId, alliance.getValue());
    }
}
