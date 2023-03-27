package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageQueryService {
    private final ResourceDataService resourceDataService;

    public int getReservedAmount(GameData gameData, UUID location, String dataId) {
        return getReservedAmount(dataId, gameData.getReservedStorages().getByLocation(location));
    }

    public int getReservedAmount(String dataId, List<ReservedStorage> reservedStorages) {
        return reservedStorages.stream()
            .filter(reservedStorage -> reservedStorage.getDataId().equals(dataId))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    public int getReservedAmount(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = fetchResourceIdsForStorageType(storageType);

        return gameData.getReservedStorages()
            .getByLocation(location)
            .stream()
            .filter(reservedStorage -> dataIdsByStorageType.contains(reservedStorage.getDataId())) //TODO filter reserved storage for production - if needed
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    private List<String> fetchResourceIdsForStorageType(StorageType storageType) {
        return resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .collect(Collectors.toList());
    }

    public int getReservedStorageCapacity(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = fetchResourceIdsForStorageType(storageType);

        return gameData.getReservedStorages()
            .getByLocation(location)
            .stream()
            .filter(reservedStorage -> dataIdsByStorageType.contains(reservedStorage.getDataId())) //TODO filter reserved storage for production - if needed
            .map(this::getReservedStorageCapacity)
            .mapToInt(Integer::intValue)
            .sum();
    }

    private int getReservedStorageCapacity(ReservedStorage reservedResource) {
        ResourceData resourceData = resourceDataService.get(reservedResource.getDataId());
        return reservedResource.getAmount() * resourceData.getMass();
    }
}
