package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingEditionService {
    private final GameDao gameDao;
    private final FreeStorageQueryService freeStorageQueryService;
    private final ResourceDataService resourceDataService;
    private final StorageSettingsModelValidator storageSettingsModelValidator;

    public void edit(UUID userId, UUID planetId, StorageSettingModel request) {
        storageSettingsModelValidator.validate(request);

        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        StorageSetting storageSetting = planet.getStorageDetails()
            .getStorageSettings()
            .findByStorageSettingId(request.getStorageSettingId())
            .orElseThrow(() -> RestException.createNonTranslated(HttpStatus.NOT_FOUND, "StorageSetting not found with id " + request.getStorageSettingId()));

        storageSetting.setPriority(request.getPriority());
        storageSetting.setBatchSize(request.getBatchSize());

        ReservedStorage reservedStorage = planet.getStorageDetails()
            .getReservedStorages()
            .findByExternalReferenceValidated(request.getStorageSettingId());

        int oldTargetAmount = storageSetting.getTargetAmount();
        int maxTargetAmount = freeStorageQueryService.getFreeStorage(planet, resourceDataService.get(request.getDataId()).getStorageType()) + reservedStorage.getAmount();
        int newTargetAmount = Math.min(maxTargetAmount, request.getTargetAmount());
        storageSetting.setTargetAmount(newTargetAmount);

        int newReservedStorage = newTargetAmount - oldTargetAmount + reservedStorage.getAmount();
        reservedStorage.setAmount(Math.max(0, newReservedStorage));
    }
}
