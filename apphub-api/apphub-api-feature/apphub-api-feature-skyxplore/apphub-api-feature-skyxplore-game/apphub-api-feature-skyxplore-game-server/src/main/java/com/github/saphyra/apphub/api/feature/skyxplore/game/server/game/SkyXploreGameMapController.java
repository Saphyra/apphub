package com.github.saphyra.apphub.api.feature.skyxplore.game.server.game;

import com.github.saphyra.apphub.api.feature.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SkyXploreGameMapController {
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_MAP)
    MapResponse getMap(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}
