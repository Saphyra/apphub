package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.StorageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class AvailableStorageMapper {
    private final StoredResourceCounter storedResourceCounter;
    private final ReservedStorageCounter reservedStorageCounter;
    private final StorageCalculator storageCalculator;

    Map<String, Integer> countAvailableStorage(List<Building> buildings, StorageDetails storageDetails) {
        return Arrays.stream(StorageType.values())
            .collect(Collectors.toMap(Enum::name, storageType -> countAvailableStorage(storageType, buildings, storageDetails)));
    }

    private int countAvailableStorage(StorageType storageType, List<Building> buildings, StorageDetails storageDetails) {
        int storedResourceAmount = storedResourceCounter.countStoredResources(storageType, storageDetails.getStoredResources());
        int reservedStorageAmount = reservedStorageCounter.countReservedStorage(storageType, storageDetails.getReservedStorages());
        int usedStorage = storedResourceAmount + reservedStorageAmount;
        int capacity = storageCalculator.calculateCapacity(storageType, buildings);

        return capacity - usedStorage;
    }
}
