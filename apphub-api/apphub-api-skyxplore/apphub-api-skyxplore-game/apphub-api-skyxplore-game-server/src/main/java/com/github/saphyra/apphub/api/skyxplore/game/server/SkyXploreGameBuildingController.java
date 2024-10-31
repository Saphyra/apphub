package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreGameBuildingController {
    @Deprecated(forRemoval = true)
    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW)
    void constructNewBuilding(@RequestBody OneParamRequest<String> dataId, @PathVariable("planetId") UUID planetId, @PathVariable("surfaceId") UUID surfaceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @Deprecated(forRemoval = true)
    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_BUILDING_UPGRADE)
    void upgradeBuilding(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION)
    void cancelConstruction(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_BUILDING_DECONSTRUCT)
    @Deprecated(forRemoval = true)
    void deconstructBuilding(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION)
    void cancelDeconstruction(@PathVariable("planetId") UUID planetId, @PathVariable("buildingId") UUID buildingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
