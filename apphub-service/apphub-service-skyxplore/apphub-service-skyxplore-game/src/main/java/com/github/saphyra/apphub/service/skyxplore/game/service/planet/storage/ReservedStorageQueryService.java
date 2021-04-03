package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageQueryService {
    private final ResourceDataService resourceDataService;

    public int getReservedStorageAmount(String dataId, Planet planet) {
        return getReservedStorageAmount(dataId, planet.getStorageDetails().getReservedStorages());
    }

    public int getReservedStorageAmount(String dataId, List<ReservedStorage> reservedStorages) {
        return reservedStorages.stream()
            .filter(reservedStorage -> reservedStorage.getDataId().equals(dataId))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    public int getReservedStorageAmount(Planet planet, StorageType storageType) {
        List<String> dataIdsByStorageType = resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .collect(Collectors.toList());

        return planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> dataIdsByStorageType.contains(reservedStorage.getDataId()))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }
}
