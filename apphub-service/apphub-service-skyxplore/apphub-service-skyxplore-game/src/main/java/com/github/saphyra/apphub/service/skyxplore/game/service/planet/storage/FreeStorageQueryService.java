package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FreeStorageQueryService {
    private final StorageCalculator storageCalculator;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final ReservedStorageQueryService reservedStorageQueryService;

    public int getFreeStorage(Planet planet, StorageType storageType) {
        return storageCalculator.calculateCapacity(planet, storageType)
            - actualResourceAmountQueryService.getActualStorageAmount(planet, storageType)
            - reservedStorageQueryService.getReservedStorageCapacity(planet, storageType);
    }
}
