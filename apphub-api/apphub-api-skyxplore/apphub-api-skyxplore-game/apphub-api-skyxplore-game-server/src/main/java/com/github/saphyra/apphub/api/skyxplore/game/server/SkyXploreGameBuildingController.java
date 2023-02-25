package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreGameBuildingController {
    @PutMapping(Endpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW)
    SurfaceResponse constructNewBuilding(@RequestBody OneParamRequest<String> dataId, @PathVariable("planetId") UUID planetId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_BUILDING_UPGRADE)
    SurfaceResponse upgradeBuilding(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION)
    SurfaceResponse cancelConstruction(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.SKYXPLORE_BUILDING_DECONSTRUCT)
    SurfaceResponse deconstructBuilding(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @DeleteMapping(Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION)
    SurfaceResponse cancelDeconstruction(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
