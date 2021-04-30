package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingCreationService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final StorageSettingFactory storageSettingFactory;

    public void createStorageSetting(UUID userId, UUID planetId, StorageSettingModel request) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        storageSettingsModelValidator.validate(request, planet);

        StorageSetting storageSetting = storageSettingFactory.create(request, request.getTargetAmount(), planetId, LocationType.PLANET);
        log.debug("StorageSetting created: {}", storageSetting);

        planet.getStorageDetails()
            .getStorageSettings()
            .add(storageSetting);
    }
}
