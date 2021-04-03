package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.UUID;

import org.springframework.web.bind.annotation.RestController;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXplorePlanetStorageSettingController;
import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
class SkyXplorePlanetStorageSettingControllerImpl implements SkyXplorePlanetStorageSettingController {
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;
    private final StorageSettingCreationService storageSettingCreationService;
    private final StorageSettingDeletionService storageSettingDeletionService;
    private final StorageSettingEditionService storageSettingEditionService;

    @Override
    //TODO unt test
    //TODO api test
    public StorageSettingsResponse getStorageSettings(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storageSettings of planet {}", accessTokenHeader.getUserId(), planetId);
        return storageSettingsResponseQueryService.getStorageSettings(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unt test
    //TODO api test
    public void createStorageSetting(StorageSettingModel request, UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create storageSetting for resource {} on planet {}", accessTokenHeader.getUserId(), request.getDataId(), planetId);
        storageSettingCreationService.createStorageSetting(accessTokenHeader.getUserId(), planetId, request);
    }

    @Override
    //TODO unt test
    //TODO api test
    public void deleteStorageSetting(UUID planetId, UUID storageSettingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete storageSetting {} from planet {}", accessTokenHeader.getUserId(), storageSettingId, planetId);
        storageSettingDeletionService.deleteStorageSetting(accessTokenHeader.getUserId(), planetId, storageSettingId);
    }

    @Override
    //TODO unt test
    //TODO api test
    public void editStorageSetting(StorageSettingModel request, UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit storageSetting {} on planet {}", accessTokenHeader.getUserId(), request.getStorageSettingId(), planetId);
        storageSettingEditionService.edit(accessTokenHeader.getUserId(), planetId,  request);
    }
}