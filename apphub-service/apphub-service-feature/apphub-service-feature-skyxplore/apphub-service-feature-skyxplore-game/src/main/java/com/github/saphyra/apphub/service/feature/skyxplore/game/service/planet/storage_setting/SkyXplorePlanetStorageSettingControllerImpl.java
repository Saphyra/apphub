package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.SkyXplorePlanetStorageSettingController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class SkyXplorePlanetStorageSettingControllerImpl implements SkyXplorePlanetStorageSettingController {
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;
    private final StorageSettingCreationService storageSettingCreationService;
    private final StorageSettingDeletionService storageSettingDeletionService;
    private final StorageSettingEditionService storageSettingEditionService;

    @Override
    public List<StorageSettingApiModel> getStorageSettings(UUID planetId, AccessToken accessToken) {
        log.info("{} wants to know the storageSettings of planet {}", accessToken.getUserId(), planetId);
        return storageSettingsResponseQueryService.getStorageSettings(accessToken.getUserId(), planetId);
    }

    @Override
    public List<StorageSettingApiModel> createStorageSetting(StorageSettingApiModel request, UUID planetId, AccessToken accessToken) {
        log.info("{} wants to create storageSetting for resource {} on planet {}", accessToken.getUserId(), request.getDataId(), planetId);
        return storageSettingCreationService.createStorageSetting(accessToken.getUserId(), planetId, request);
    }

    @Override
    public List<StorageSettingApiModel> deleteStorageSetting(UUID storageSettingId, AccessToken accessToken) {
        log.info("{} wants to delete storageSetting {}", accessToken.getUserId(), storageSettingId);
        return storageSettingDeletionService.deleteStorageSetting(accessToken.getUserId(), storageSettingId);
    }

    @Override
    public List<StorageSettingApiModel> editStorageSetting(StorageSettingApiModel request, AccessToken accessToken) {
        log.info("{} wants to edit storageSetting {}", accessToken.getUserId(), request.getStorageSettingId());
        return storageSettingEditionService.edit(accessToken.getUserId(), request);
    }
}
