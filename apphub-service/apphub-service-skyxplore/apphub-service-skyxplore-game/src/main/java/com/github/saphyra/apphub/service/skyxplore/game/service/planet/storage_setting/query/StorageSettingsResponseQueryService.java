package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingsResponseQueryService {
    private final GameDao gameDao;
    private final AvailableResourcesMapper availableResourcesMapper;
    private final CurrentSettingsMapper currentSettingsMapper;
    private final AvailableStorageMapper availableStorageMapper;

    public StorageSettingsResponse getStorageSettings(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        List<StorageSetting> storageSettings = planet.getStorageDetails()
            .getStorageSettings();

        return StorageSettingsResponse.builder()
            .currentSettings(currentSettingsMapper.convert(storageSettings))
            .availableResources(availableResourcesMapper.getAvailableResources(storageSettings))
            .availableStorage(availableStorageMapper.countAvailableStorage(planet.getBuildings(), planet.getStorageDetails()))
            .build();
    }
}
