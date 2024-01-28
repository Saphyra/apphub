package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXplorePlanetStorageSettingController;
import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
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
    public List<StorageSettingApiModel> getStorageSettings(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storageSettings of planet {}", accessTokenHeader.getUserId(), planetId);
        return storageSettingsResponseQueryService.getStorageSettings(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public List<StorageSettingApiModel> createStorageSetting(StorageSettingApiModel request, UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create storageSetting for resource {} on planet {}", accessTokenHeader.getUserId(), request.getDataId(), planetId);
        return storageSettingCreationService.createStorageSetting(accessTokenHeader.getUserId(), planetId, request);
    }

    @Override
    public List<StorageSettingApiModel> deleteStorageSetting(UUID storageSettingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete storageSetting {}", accessTokenHeader.getUserId(), storageSettingId);
        return storageSettingDeletionService.deleteStorageSetting(accessTokenHeader.getUserId(), storageSettingId);
    }

    @Override
    public List<StorageSettingApiModel> editStorageSetting(StorageSettingApiModel request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit storageSetting {}", accessTokenHeader.getUserId(), request.getStorageSettingId());
        return storageSettingEditionService.edit(accessTokenHeader.getUserId(), request);
    }
}
