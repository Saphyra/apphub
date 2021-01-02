package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingsModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.query.PlanetStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageSettingCreationService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final PlanetStorageQueryService planetStorageQueryService;
    private final ResourceDataService resourceDataService;
    private final StorageSettingFactory storageSettingFactory;
    private final ReservedStorageFactory reservedStorageFactory;

    public void createStorageSetting(UUID userId, UUID planetId, StorageSettingsModel request) {
        storageSettingsModelValidator.validate(request);

        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        if (planet.getStorageDetails().getStorageSettings().findByDataId(request.getDataId()).isPresent()) {
            throw new RuntimeException("StorageSetting for dataId " + request.getDataId() + " already exists.");
        }

        ResourceData resourceData = resourceDataService.get(request.getDataId());

        int currentAmount = planetStorageQueryService.getUsableStoredResourceAmount(request.getDataId(), planet);
        int missingAmount = request.getTargetAmount() - currentAmount;
        int freeCapacity = planetStorageQueryService.getFreeStorage(planet, resourceData.getStorageType());

        int targetAmount = Math.min(missingAmount, freeCapacity);

        StorageSetting storageSetting = storageSettingFactory.create(request, targetAmount);
        log.debug("StorageSetting created: {}", storageSetting);

        planet.getStorageDetails()
            .getStorageSettings()
            .add(storageSetting);

        ReservedStorage reservedStorage = reservedStorageFactory.create(storageSetting.getStorageSettingsId(), request.getDataId(), missingAmount);

        log.debug("ReservedStorage created: {}", reservedStorage);
        planet.getStorageDetails()
            .getReservedStorages()
            .add(reservedStorage);
    }
}
