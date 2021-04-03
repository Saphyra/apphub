package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FreeStorageQueryService {
    private final StorageCalculator storageCalculator;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;
    private final ReservedStorageQueryService reservedStorageQueryService;

    public int getUsableStoredResourceAmount(String dataId, Planet planet) {
        return actualResourceAmountQueryService.getActualAmount(dataId, planet) - allocatedResourceAmountQueryService.getAllocatedResourceAmount(dataId, planet);
    }

    public int getFreeStorage(Planet planet, StorageType storageType) {
        return storageCalculator.calculateCapacity(planet, storageType) - actualResourceAmountQueryService.getActualAmount(planet, storageType) - reservedStorageQueryService.getReservedStorageAmount(planet, storageType);
    }
}
