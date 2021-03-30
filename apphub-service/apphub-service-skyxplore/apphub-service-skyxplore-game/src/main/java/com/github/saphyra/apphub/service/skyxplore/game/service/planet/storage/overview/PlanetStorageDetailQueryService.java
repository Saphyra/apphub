package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageTypeResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PlanetStorageDetailQueryService {
    private final StorageCalculator storageCalculator;
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;
    private final ResourceDetailsQueryService resourceDetailsQueryService;

    StorageTypeResponse getStorageDetails(Planet planet, StorageType storageType) {
        return StorageTypeResponse.builder()
            .capacity(storageCalculator.calculateCapacity(planet, storageType))
            .reservedStorageAmount(reservedStorageQueryService.getReservedStorageAmount(planet, storageType))
            .actualResourceAmount(actualResourceAmountQueryService.getActualAmount(planet, storageType))
            .allocatedResourceAmount(allocatedResourceAmountQueryService.getAllocatedResourceAmount(planet, storageType))
            .resourceDetails(resourceDetailsQueryService.getResourceDetails(planet, storageType))
            .build();
    }
}
