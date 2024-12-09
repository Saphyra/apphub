package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO role protection test
public interface SkyXplorePlanetSurfaceConstructionAreaController {
    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCT_CONSTRUCTION_AREA)
    void constructConstructionArea(@RequestBody OneParamRequest<String> constructionAreaDataId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION)
    void cancelConstructionAreaConstruction(@PathVariable("constructionId") UUID constructionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA)
    void deconstructConstructionArea(@PathVariable("constructionAreaId") UUID constructionAreaId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA)
    void cancelDeconstructConstructionArea(@PathVariable("deconstructionId") UUID deconstructionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * @return dataIds of building modules can be built on the given construction area slot
     */
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_AVAILABLE_BUILDING_MODULES)
    List<String> getAvailableBuildingModules(@PathVariable("constructionAreaId") UUID constructionAreaId, @PathVariable("buildingModuleCategory") String buildingModuleCategory, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
