package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageQueryService {
    private final ResourceDataService resourceDataService;

    public int getReservedAmount(String dataId, Planet planet) {
        return getReservedAmount(dataId, planet.getStorageDetails().getReservedStorages());
    }

    public int getReservedAmount(String dataId, List<ReservedStorage> reservedStorages) {
        return reservedStorages.stream()
            .filter(reservedStorage -> reservedStorage.getDataId().equals(dataId))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    public int getReservedAmount(Planet planet, StorageType storageType) {
        List<String> dataIdsByStorageType = fetchResourceIdsForStorageType(storageType);

        return planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> dataIdsByStorageType.contains(reservedStorage.getDataId()))
            .filter(reservedStorage -> reservedStorage.getLocationType() != LocationType.PRODUCTION)
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    private List<String> fetchResourceIdsForStorageType(StorageType storageType) {
        return resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .collect(Collectors.toList());
    }

    public int getReservedStorageCapacity(Planet planet, StorageType storageType) {
        List<String> dataIdsByStorageType = fetchResourceIdsForStorageType(storageType);

        return planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> dataIdsByStorageType.contains(reservedStorage.getDataId()))
            .filter(reservedStorage -> reservedStorage.getLocationType() != LocationType.PRODUCTION)
            .map(this::getReservedStorageCapacity)
            .mapToInt(Integer::intValue)
            .sum();
    }

    private int getReservedStorageCapacity(ReservedStorage reservedResource) {
        ResourceData resourceData = resourceDataService.get(reservedResource.getDataId());
        return reservedResource.getAmount() * resourceData.getMass();
    }
}
