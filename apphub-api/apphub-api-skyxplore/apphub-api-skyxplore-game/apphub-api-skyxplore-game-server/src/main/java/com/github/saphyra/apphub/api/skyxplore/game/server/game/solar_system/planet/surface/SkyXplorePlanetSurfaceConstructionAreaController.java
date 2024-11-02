package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXplorePlanetSurfaceConstructionAreaController {
    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCT_CONSTRUCTION_AREA)
        //TODO API test
        //TODO role protection test
    void constructConstructionArea(@RequestBody OneParamRequest<String> constructionAreaDataId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION)
        //TODO API test
        //TODO role protection test
    void cancelConstructionAreaConstruction(@PathVariable("constructionId") UUID constructionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA)
        //TODO API test
        //TODO role protection test
    void deconstructConstructionArea(@PathVariable("constructionAreaId") UUID constructionAreaId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA)
    void cancelDeconstructConstructionArea(@PathVariable("deconstructionId") UUID deconstructionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
