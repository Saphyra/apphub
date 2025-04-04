package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreGameSolarSystemController {
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GET_SOLAR_SYSTEM)
    SolarSystemResponse getSolarSystem(@PathVariable("solarSystemId") UUID solarSystemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_SOLAR_SYSTEM_RENAME)
    void renameSolarSystem(@RequestBody OneParamRequest<String> solarSystemName, @PathVariable("solarSystemId") UUID solarSystemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
