package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingEditionService {
    private final GameDao gameDao;
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

        storageSetting.setTargetAmount(request.getTargetAmount());
    }
}
