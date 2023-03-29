package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbySettingsController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

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
}
