package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreGameSolarSystemController {
    @GetMapping(Endpoints.SKYXPLORE_GET_SOLAR_SYSTEM)
    SolarSystemResponse getMap(@PathVariable("solarSystemId") UUID solarSystemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
