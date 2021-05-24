package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

public interface SkyXplorePriorityController {
    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_PRIORITIES)
    Map<String, Integer> getPriorities(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_PLANET_UPDATE_PRIORITY)
    void updatePriority(@RequestBody OneParamRequest<Integer> newPriority, @PathVariable("planetId") UUID planetId, @PathVariable("priorityType") String priorityType, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
