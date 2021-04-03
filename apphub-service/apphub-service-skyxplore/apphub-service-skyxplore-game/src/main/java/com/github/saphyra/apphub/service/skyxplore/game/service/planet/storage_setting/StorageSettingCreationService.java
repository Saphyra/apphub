package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingCreationService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final FreeStorageQueryService freeStorageQueryService;
    private final ResourceDataService resourceDataService;
    private final StorageSettingFactory storageSettingFactory;
    private final ReservedStorageFactory reservedStorageFactory;

    public void createStorageSetting(UUID userId, UUID planetId, StorageSettingModel request) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        storageSettingsModelValidator.validate(request, planet);

        ResourceData resourceData = resourceDataService.get(request.getDataId());

        int currentAmount = freeStorageQueryService.getUsableStoredResourceAmount(request.getDataId(), planet);
        int missingAmount = request.getTargetAmount() - currentAmount;
        int freeCapacity = freeStorageQueryService.getFreeStorage(planet, resourceData.getStorageType());

        int targetAmount = Math.min(missingAmount, freeCapacity);

        StorageSetting storageSetting = storageSettingFactory.create(request, targetAmount, planetId, LocationType.PLANET);
        log.debug("StorageSetting created: {}", storageSetting);

        planet.getStorageDetails()
            .getStorageSettings()
            .add(storageSetting);

        ReservedStorage reservedStorage = reservedStorageFactory.create(storageSetting.getStorageSettingId(), request.getDataId(), missingAmount);

        log.debug("ReservedStorage created: {}", reservedStorage);
        planet.getStorageDetails()
            .getReservedStorages()
            .add(reservedStorage);
    }
}
