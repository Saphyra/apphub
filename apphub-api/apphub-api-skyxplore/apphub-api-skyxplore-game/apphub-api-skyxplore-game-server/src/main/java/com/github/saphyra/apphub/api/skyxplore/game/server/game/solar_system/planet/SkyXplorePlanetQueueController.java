package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXplorePlanetQueueController {
    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY)
    void setItemPriority(@RequestBody OneParamRequest<Integer> priority, @PathVariable("planetId") UUID planetId, @PathVariable("type") String type, @PathVariable("itemId") UUID itemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM)
    void cancelItem(@PathVariable("planetId") UUID planetId, @PathVariable("type") String type, @PathVariable("itemId") UUID itemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
