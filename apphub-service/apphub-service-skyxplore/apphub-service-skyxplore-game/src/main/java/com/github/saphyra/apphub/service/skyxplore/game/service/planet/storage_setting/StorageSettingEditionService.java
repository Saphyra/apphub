package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingsModel;
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
public class StorageSettingEditionService {
    private final GameDao gameDao;
    private final PlanetStorageQueryService planetStorageQueryService;
    private final ResourceDataService resourceDataService;

    public void edit(UUID userId, UUID planetId, UUID storageSettingId, StorageSettingsModel request/*TODO validate*/) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        StorageSetting storageSetting = planet.getStorageDetails()
            .getStorageSettings()
            .findByStorageSettingId(storageSettingId)
            .orElseThrow(() -> new RuntimeException("StorageSetting not found with id " + storageSettingId)); //TODO proper exception

        ReservedStorage reservedStorage = planet.getStorageDetails()
            .getReservedStorages()
            .findByExternalReferenceValidated(storageSettingId);

        storageSetting.setPriority(request.getPriority());
        storageSetting.setBatchSize(request.getBatchSize());

        int maxTargetAmount = planetStorageQueryService.getFreeStorage(planet, resourceDataService.get(storageSetting.getDataId()).getStorageType()) + reservedStorage.getAmount();
        int targetAmount = storageSetting.getTargetAmount();
        int newTargetAmount = Math.min(maxTargetAmount, request.getTargetAmount());
        storageSetting.setTargetAmount(newTargetAmount);

        int newReservedStorage = newTargetAmount - targetAmount + reservedStorage.getAmount();
        reservedStorage.setAmount(Math.max(0, newReservedStorage));
    }
}
