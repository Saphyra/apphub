package com.github.saphyra.apphub.api.skyxplore.game.server;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;

public interface SkyXplorePlanetStorageSettingController {
    @GetMapping(Endpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS)
    StorageSettingsResponse getStorageSettings(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING)
    void createStorageSetting(@RequestBody StorageSettingModel request, @PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING)
    void deleteStorageSetting(@PathVariable("planetId") UUID planetId, @PathVariable("storageSettingId") UUID storageSettingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING)
    void editStorageSetting(@RequestBody StorageSettingModel request, @PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}