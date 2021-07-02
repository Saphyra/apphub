package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("skyxplore-data-saved-game")
public interface SkyXploreSavedGameApiClient {
    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION)
    GameViewForLobbyCreation getGameForLobbyCreation(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
