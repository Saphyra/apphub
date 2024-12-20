package com.github.saphyra.apphub.api.skyxplore.game.server.platform;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreGameController {
    /**
     * Used by page controllers to redirect the user to the proper page, and SkyXplore data to fetch the current game-id of a player
     */
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GET_GAME_ID_OF_USER)
    OneParamResponse<UUID> getGameId(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * If the game is abandoned by the users, it will be shut down and removed from the memory eventually
     */
    @PostMapping(GenericSkyXploreEndpoints.EVENT_SKYXPLORE_GAME_CLEANUP)
    void cleanUpExpiredGames();

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_EXIT_GAME)
    void exitGame(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_PAUSE)
    void pauseGame(@RequestBody OneParamRequest<Boolean> paused, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Saved game is marked for deletion. Triggering the game removal process.
     */
    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId);

    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_IS_HOST)
    OneParamResponse<Boolean> isHost(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_SAVE)
    void saveGame(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
