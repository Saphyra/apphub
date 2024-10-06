package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXplorePlanetStorageSettingController {
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS)
    List<StorageSettingApiModel> getStorageSettings(@PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING)
    List<StorageSettingApiModel> createStorageSetting(@RequestBody StorageSettingApiModel request, @PathVariable("planetId") UUID planetId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING)
    List<StorageSettingApiModel> deleteStorageSetting(@PathVariable("storageSettingId") UUID storageSettingId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(SkyXploreGameEndpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING)
    List<StorageSettingApiModel> editStorageSetting(@RequestBody StorageSettingApiModel request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
