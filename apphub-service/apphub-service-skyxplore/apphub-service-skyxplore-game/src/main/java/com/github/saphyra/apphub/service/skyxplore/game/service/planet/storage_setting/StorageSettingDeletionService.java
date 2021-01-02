package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageSettingDeletionService {
    private final GameDao gameDao;

    public void deleteStorageSetting(UUID userId, UUID planetId, UUID storageSettingId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        planet.getStorageDetails()
            .getStorageSettings()
            .deleteByStorageSettingId(storageSettingId);

        planet.getStorageDetails()
            .getReservedStorages()
            .deleteByExternalReference(storageSettingId);
    }
}
