package com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
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
public interface SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleController {
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_GET_BUILDING_MODULES)
    List<BuildingModuleResponse> getBuildingModules(@PathVariable("constructionAreaId") UUID constructionAreaId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CONSTRUCT_BUILDING_MODULE)
    List<BuildingModuleResponse> constructBuildingModule(
        @RequestBody OneParamRequest<String> buildingModuleDataId,
        @PathVariable("constructionAreaId") UUID constructionAreaId,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_CONSTRUCTION_OF_BUILDING_MODULE)
    List<BuildingModuleResponse> cancelConstructionOfBuildingModule(@PathVariable("constructionId") UUID constructionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_DECONSTRUCT_BUILDING_MODULE)
    List<BuildingModuleResponse> deconstructBuildingModule(@PathVariable("buildingModuleId") UUID buildingModuleId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_DECONSTRUCTION_OF_BUILDING_MODULE)
    List<BuildingModuleResponse> cancelDeconstructionOfBuildingModule(@PathVariable("deconstructionId") UUID deconstructionId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
