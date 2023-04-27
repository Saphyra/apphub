package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbySettingsController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance.AllianceService;
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
    public void editSettings(SkyXploreGameSettings settings, AccessTokenHeader accessTokenHeader) {
        editSettingsService.editSettings(accessTokenHeader.getUserId(), settings);
    }

    @Override
    public SkyXploreGameSettings getGameSettings(AccessTokenHeader accessTokenHeader) {
        return lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getSettings();
    }

    @Override
    public void createOrModifyAi(AiPlayer aiPlayer, AccessTokenHeader accessTokenHeader) {
        aiService.createOrModifyAi(accessTokenHeader.getUserId(), aiPlayer);
    }

    @Override
    public void removeAi(UUID aiUserId, AccessTokenHeader accessTokenHeader) {
        aiService.removeAi(accessTokenHeader.getUserId(), aiUserId);
    }

    @Override
    public List<AiPlayer> getAis(AccessTokenHeader accessTokenHeader) {
        return lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getAis();
    }

    @Override
    public List<AllianceResponse> getAlliancesOfLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the alliances of his lobby.", accessTokenHeader.getUserId());
        return allianceService.getAlliances(accessTokenHeader.getUserId());
    }

    @Override
    public void changeAllianceOfPlayer(OneParamRequest<String> alliance, UUID userId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change the alliance of player {} to {}", accessTokenHeader.getUserId(), userId, alliance.getValue());
        allianceService.setAllianceOfPlayer(accessTokenHeader.getUserId(), userId, alliance.getValue());
    }

    @Override
    public void changeAllianceOfAi(OneParamRequest<String> alliance, UUID userId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change the alliance of ai {} to {}", accessTokenHeader.getUserId(), userId, alliance.getValue());
        allianceService.setAllianceOfAi(accessTokenHeader.getUserId(), userId, alliance.getValue());
    }
}
