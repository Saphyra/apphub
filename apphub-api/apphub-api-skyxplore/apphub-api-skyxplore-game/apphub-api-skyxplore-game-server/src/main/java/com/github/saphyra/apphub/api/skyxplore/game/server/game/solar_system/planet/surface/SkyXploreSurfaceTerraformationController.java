package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface;

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

public interface SkyXploreSurfaceTerraformationController {
    /**
     * Starting terraformation of a given surface
     *
     * @param surfaceType The new type of the surface
     * @param planetId    Location of the surface
     * @param surfaceId   ID of the surface
     */
    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_TERRAFORM_SURFACE)
    void terraformSurface(@RequestBody OneParamRequest<String> surfaceType, @PathVariable("planetId") UUID planetId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Cancelling terraformation of the given surface
     */
    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_CANCEL_TERRAFORMATION)
    void cancelTerraformation(@PathVariable("planetId") UUID planetId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
