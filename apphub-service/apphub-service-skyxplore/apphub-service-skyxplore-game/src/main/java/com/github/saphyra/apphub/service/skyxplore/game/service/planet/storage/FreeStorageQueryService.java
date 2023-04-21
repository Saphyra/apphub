package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FreeStorageQueryService {
    private final StorageCalculator storageCalculator;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final ResourceDataService resourceDataService;

    public int getFreeStorage(GameData gameData, UUID location, String dataId) {
        return getFreeStorage(gameData, location, resourceDataService.get(dataId).getStorageType());
    }

    public int getFreeStorage(GameData gameData, UUID location, StorageType storageType) {
        return storageCalculator.calculateCapacity(gameData, location, storageType)
            - actualResourceAmountQueryService.getActualStorageAmount(gameData, location, storageType)
            - reservedStorageQueryService.getReservedStorageCapacity(gameData, location, storageType);
    }
}
