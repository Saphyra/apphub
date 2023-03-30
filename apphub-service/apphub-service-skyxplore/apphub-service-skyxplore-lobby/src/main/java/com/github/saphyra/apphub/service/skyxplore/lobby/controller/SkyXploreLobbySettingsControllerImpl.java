package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbySettingsController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SkyXploreLobbySettingsControllerImpl implements SkyXploreLobbySettingsController {
    @Override
    public void editSettings(SkyXploreGameSettings settings, AccessTokenHeader accessTokenHeader) {
        //TODO implement
    }

    @Override
    public SkyXploreGameSettings getGameSettings(AccessTokenHeader accessTokenHeader) {
        //TODO implement
        return null;
    }

    @Override
    public void createAi(AiPlayer aiPlayer, AccessTokenHeader accessTokenHeader) {
        //TODO implement
    }

    @Override
    public void removeAi(UUID aiUserId, AccessTokenHeader accessTokenHeader) {
        //TODO implement
    }

    @Override
    public List<AiPlayer> getAis(AccessTokenHeader accessTokenHeader) {
        //TODO implement
        return null;
    }
}
