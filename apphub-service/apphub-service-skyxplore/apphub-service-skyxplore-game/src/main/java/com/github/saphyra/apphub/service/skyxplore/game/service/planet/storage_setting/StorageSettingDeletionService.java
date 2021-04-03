package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingDeletionService {
    private final GameDao gameDao;

    public void deleteStorageSetting(UUID userId, UUID planetId, UUID storageSettingId) {
        StorageDetails storageDetails = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getStorageDetails();

        storageDetails.getStorageSettings()
            .deleteByStorageSettingId(storageSettingId);

        storageDetails.getReservedStorages()
            .deleteByExternalReference(storageSettingId);
    }
}
