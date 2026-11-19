package com.github.saphyra.apphub.api.feature.skyxplore.game.client;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

public interface SkyXploreGameApiClient {
    @GetExchange(SkyXploreGameEndpoints.SKYXPLORE_GET_GAME_ID_OF_USER)
    OneParamResponse<UUID> getGameId(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @PostExchange(GenericSkyXploreEndpoints.EVENT_SKYXPLORE_GAME_CLEANUP)
    void cleanUpExpiredGames();

    @DeleteExchange(SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_DELETE_GAME)
    void deleteGame(@PathVariable("gameId") UUID gameId);
}
