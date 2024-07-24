package com.github.saphyra.apphub.api.skyxplore.game.client;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "skyxplore-game", url = "${serviceUrls.skyxploreGame}")
public interface SkyXploreGameApiClient {
    @GetMapping(Endpoints.SKYXPLORE_GET_GAME_ID_OF_USER)
    OneParamResponse<UUID> getGameId(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.EVENT_SKYXPLORE_GAME_CLEANUP)
    void cleanUpExpiredGames(@RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
