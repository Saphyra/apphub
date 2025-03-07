package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXplorePlanetPopulationController {
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_GET_POPULATION)
    List<CitizenResponse> getPopulation(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_RENAME_CITIZEN)
    void renameCitizen(@RequestBody OneParamRequest<String> newName, @PathVariable("citizenId") UUID citizenId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
