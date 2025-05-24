package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceAmountQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetStorageDetailQueryService {
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final StoredResourceAmountQueryService storedResourceAmountQueryService;
    private final ResourceDetailsQueryService resourceDetailsQueryService;
    private final StorageCapacityService storageCapacityService;

    StorageDetailsResponse getStorageDetails(GameData gameData, UUID location, StorageType storageType) {
        return StorageDetailsResponse.builder()
            .capacity(storageCapacityService.getDepotCapacity(gameData, location, storageType))
            .reservedStorageAmount(reservedStorageQueryService.getReservedAmount(gameData, location, storageType))
            .actualResourceAmount(storedResourceAmountQueryService.getActualAmount(gameData, location, storageType))
            .allocatedResourceAmount(storageCapacityService.getAllocatedResourceAmount(gameData, location, storageType))
            .resourceDetails(resourceDetailsQueryService.getResourceDetails(gameData, location, storageType))
            .build();
    }
}
