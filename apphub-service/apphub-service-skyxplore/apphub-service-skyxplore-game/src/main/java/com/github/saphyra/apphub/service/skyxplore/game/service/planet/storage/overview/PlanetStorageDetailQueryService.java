package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetStorageDetailQueryService {
    private final StorageCapacityService storageCapacityService;
    private final ResourceDetailsQueryService resourceDetailsQueryService;

    StorageDetailsResponse getStorageDetails(GameData gameData, UUID location, StorageType storageType) {
        return StorageDetailsResponse.builder()
            .capacity(storageCapacityService.getDepotCapacity(gameData, location, storageType))
            .reservedStorageAmount(storageCapacityService.getReservedDepotCapacity(gameData, location, storageType))
            .actualResourceAmount(storageCapacityService.getOccupiedDepotStorage(gameData, location, storageType))
            .allocatedResourceAmount(storageCapacityService.getAllocatedResourceAmount(gameData, location, storageType))
            .resourceDetails(resourceDetailsQueryService.getResourceDetails(gameData, location, storageType))
            .build();
    }
}
