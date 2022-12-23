package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreGameController {
    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_IS_USER_IN_GAME)
    boolean isUserInGame(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.EVENT_SKYXPLORE_GAME_CLEANUP)
    void cleanUpExpiredGames();

    @DeleteMapping(Endpoints.SKYXPLORE_EXIT_GAME)
    void exitGame(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_GAME_PAUSE)
    void pauseGame(@RequestBody OneParamRequest<Boolean> paused, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId);
}
