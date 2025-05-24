package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
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
class ResourceDetailsResponseMapper {
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final StoredResourceAmountQueryService storedResourceAmountQueryService;
    private final StorageCapacityService storageCapacityService;

    ResourceDetailsResponse createResourceData(GameData gameData, UUID location, ResourceData resourceData) {
        return ResourceDetailsResponse.builder()
            .dataId(resourceData.getId())
            .reservedStorageAmount(reservedStorageQueryService.getReservedAmount(gameData, location, resourceData.getId()))
            .actualAmount(storedResourceAmountQueryService.getActualAmount(gameData, location, resourceData.getId()))
            .allocatedResourceAmount(storageCapacityService.getAllocatedResourceAmount(gameData, location, resourceData.getId()))
            .build();
    }
}
