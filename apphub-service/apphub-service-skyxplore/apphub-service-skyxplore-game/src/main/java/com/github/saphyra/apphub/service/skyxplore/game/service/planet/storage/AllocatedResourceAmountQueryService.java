package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AllocatedResourceAmountQueryService {
    private final ResourceDataService resourceDataService;

    public int getAllocatedResourceAmount(String dataId, Planet planet) {
        return getAllocatedResourceAmount(dataId, planet.getStorageDetails().getAllocatedResources());
    }

    public int getAllocatedResourceAmount(String dataId, List<AllocatedResource> allocatedResources) {
        return allocatedResources.stream()
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }

    public int getAllocatedResourceAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getAllocatedResources()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }
}
