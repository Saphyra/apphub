package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXploreLobbySettingsController {
    //TODO API test
    @PostMapping(Endpoints.SKYXPLORE_LOBBY_EDIT_SETTINGS)
    void editSettings(@RequestBody SkyXploreGameSettings settings, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_GAME_SETTINGS)
    SkyXploreGameSettings getGameSettings(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PutMapping(Endpoints.SKYXPLORE_LOBBY_ADD_AI)
    void createAi(@RequestBody AiPlayer aiPlayer, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @DeleteMapping(Endpoints.SKYXPLORE_LOBBY_REMOVE_AI)
    void removeAi(@PathVariable("userId") UUID aiUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @GetMapping(Endpoints.SKYXPLORE_LOBBY_GET_AIS)
    List<AiPlayer> getAis(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
