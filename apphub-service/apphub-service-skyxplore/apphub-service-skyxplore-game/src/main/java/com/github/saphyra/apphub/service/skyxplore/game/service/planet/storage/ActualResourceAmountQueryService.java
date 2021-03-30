package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ActualResourceAmountQueryService {
    private final ResourceDataService resourceDataService;

    public int getActualAmount(String dataId, Planet planet) {
        return getActualAmount(dataId, planet.getStorageDetails().getStoredResources());
    }

    public int getActualAmount(String dataId, List<StoredResource> storedResources) {
        return storedResources.stream()
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getActualAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getStoredResources()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }
}
