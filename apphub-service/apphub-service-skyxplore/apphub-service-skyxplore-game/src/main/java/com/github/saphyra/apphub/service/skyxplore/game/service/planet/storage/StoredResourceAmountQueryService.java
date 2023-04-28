package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourceAmountQueryService {
    private final ResourceDataService resourceDataService;

    public int getActualAmount(GameData gameData, UUID location, String dataId) {
        return gameData.getStoredResources()
            .findByLocationAndDataId(location, dataId)
            .map(StoredResource::getAmount)
            .orElse(0);
    }

    public int getActualAmount(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = fetchResourceIdsForStorageType(storageType);
        log.info("DataIds for StorageType {}: {}", storageType, dataIdsByStorageType);

        log.info("StoredResources: {}", gameData.getStoredResources());

        return gameData.getStoredResources()
            .getByLocation(location)
            .stream()
            .peek(storedResource -> log.info("Found: {}", storedResource))
            .filter(storedResource -> dataIdsByStorageType.contains(storedResource.getDataId()))
            .peek(storedResource -> log.info("For StorageType {}: {}", storedResource, storedResource))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    private List<String> fetchResourceIdsForStorageType(StorageType storageType) {
        return resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .collect(Collectors.toList());
    }

    public int getActualStorageAmount(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = fetchResourceIdsForStorageType(storageType);

        return gameData.getStoredResources()
            .getByLocation(location)
            .stream()
            .filter(storedResource -> dataIdsByStorageType.contains(storedResource.getDataId()))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }
}
