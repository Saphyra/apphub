package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetStorageDetailQueryService {
    private final StorageCalculator storageCalculator;
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;
    private final ResourceDetailsQueryService resourceDetailsQueryService;

    StorageDetailsResponse getStorageDetails(GameData gameData, UUID location, StorageType storageType) {
        return StorageDetailsResponse.builder()
            .capacity(storageCalculator.calculateCapacity(gameData, location, storageType))
            .reservedStorageAmount(reservedStorageQueryService.getReservedAmount(gameData, location, storageType))
            .actualResourceAmount(actualResourceAmountQueryService.getActualAmount(gameData, location, storageType))
            .allocatedResourceAmount(allocatedResourceAmountQueryService.getAllocatedResourceAmount(gameData, location, storageType))
            .resourceDetails(resourceDetailsQueryService.getResourceDetails(gameData, location, storageType))
            .build();
    }
}
