package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceDetailsResponseMapper {
    private final ReservedStorageQueryService reservedStorageQueryService;
    private final ActualResourceAmountQueryService actualResourceAmountQueryService;
    private final AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;

    ResourceDetailsResponse createResourceData(ResourceData resourceData, StorageDetails storageDetails) {
        return ResourceDetailsResponse.builder()
            .dataId(resourceData.getId())
            .reservedStorageAmount(reservedStorageQueryService.getReservedAmount(resourceData.getId(), storageDetails.getReservedStorages()))
            .actualAmount(actualResourceAmountQueryService.getActualAmount(resourceData.getId(), storageDetails.getStoredResources()))
            .allocatedResourceAmount(allocatedResourceAmountQueryService.getAllocatedResourceAmount(resourceData.getId(), storageDetails.getAllocatedResources()))
            .build();
    }
}
