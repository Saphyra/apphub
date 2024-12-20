package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXplorePlanetPriorityController {
    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_UPDATE_PRIORITY)
    void updatePriority(@RequestBody OneParamRequest<Integer> newPriority, @PathVariable("planetId") UUID planetId, @PathVariable("priorityType") String priorityType, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
