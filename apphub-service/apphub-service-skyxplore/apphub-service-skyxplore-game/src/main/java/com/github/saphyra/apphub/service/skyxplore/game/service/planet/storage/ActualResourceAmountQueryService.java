package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActualResourceAmountQueryService {
    private final ResourceDataService resourceDataService;

    public int getActualAmount(String dataId, Planet planet) {
        return getActualAmount(dataId, planet.getStorageDetails().getStoredResources());
    }

    public int getActualAmount(String dataId, Map<String, StoredResource> storedResources) {
        return Optional.ofNullable(storedResources.get(dataId))
            .map(StoredResource::getAmount)
            .orElse(0);
    }

    public int getActualAmount(Planet planet, StorageType storageType) {
        List<String> dataIdsByStorageType = resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .collect(Collectors.toList());

        return planet.getStorageDetails()
            .getStoredResources()
            .values()
            .stream()
            .filter(reservedStorage -> dataIdsByStorageType.contains(reservedStorage.getDataId()))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }
}
