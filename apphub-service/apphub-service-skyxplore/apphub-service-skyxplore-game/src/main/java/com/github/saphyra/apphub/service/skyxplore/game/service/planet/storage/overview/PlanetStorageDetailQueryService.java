package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetStorageDetailQueryService {
    private final StorageCalculator storageCalculator;
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;
    private final ResourceDetailsQueryService resourceDetailsQueryService;

    StorageDetailsResponse getStorageDetails(Planet planet, StorageType storageType) {
        return StorageDetailsResponse.builder()
            .capacity(storageCalculator.calculateCapacity(planet, storageType))
            .reservedStorageAmount(reservedStorageQueryService.getReservedAmount(planet, storageType))
            .actualResourceAmount(actualResourceAmountQueryService.getActualAmount(planet, storageType))
            .allocatedResourceAmount(allocatedResourceAmountQueryService.getAllocatedResourceAmount(planet, storageType))
            .resourceDetails(resourceDetailsQueryService.getResourceDetails(planet, storageType))
            .build();
    }
}
