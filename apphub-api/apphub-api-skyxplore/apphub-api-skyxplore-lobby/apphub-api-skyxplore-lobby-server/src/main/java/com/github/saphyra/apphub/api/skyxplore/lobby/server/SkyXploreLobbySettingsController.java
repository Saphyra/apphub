package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SkyXploreLobbySettingsController {
    @PostMapping(Endpoints.SKYXPLORE_LOBBY_EDIT_SETTINGS)
    void editSettings(@RequestBody SkyXploreGameSettings settings, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS)
    SkyXploreGameSettings getGameSettings(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
