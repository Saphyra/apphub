package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingsModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SkyXploreGamePlanetController {
    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_SURFACE)
    List<SurfaceResponse> getSurfaceOfPlanet(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_STORAGE)
    PlanetStorageResponse getStorageOfPlanet(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_POPULATION_OVERVIEW)
    PlanetPopulationOverviewResponse getPopulationOverview(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_BUILDING_OVERVIEW)
    Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_PRIORITIES)
    Map<String, Integer> getPriorities(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS)
    StorageSettingsResponse getStorageSettings(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING)
    void createStorageSetting(@RequestBody StorageSettingsModel request, @PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING)
    void deleteStorageSetting(@PathVariable("planetId") UUID planetId, @PathVariable("storageSettingId") UUID storageSettingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING)
    void editStorageSetting(@RequestBody StorageSettingsModel request, @PathVariable("planetId") UUID planetId, @PathVariable("storageSettingId") UUID storageSettingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_POPULATION)
    List<CitizenResponse> getPopulation(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
