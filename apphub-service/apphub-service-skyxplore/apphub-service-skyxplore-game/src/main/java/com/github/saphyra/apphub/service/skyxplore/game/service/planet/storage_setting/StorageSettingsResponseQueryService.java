package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageSettingsResponseQueryService {
    private final GameDao gameDao;
    private final ResourceDataService resourceDataService;
    private final StorageBuildingService storageBuildingService;

    public StorageSettingsResponse getStorageSettings(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);

        List<StorageSetting> storageSettings = planet.getStorageDetails()
            .getStorageSettings();

        return StorageSettingsResponse.builder()
            .currentSettings(convert(storageSettings))
            .availableResources(getAvailableResources(storageSettings))
            .availableStorage(countAvailableStorage(planet.getBuildings(), planet.getStorageDetails()))
            .build();
    }

    private List<StorageSettingModel> convert(List<StorageSetting> storageSettings) {
        return storageSettings.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private StorageSettingModel convert(StorageSetting storageSetting) {
        return StorageSettingModel.builder()
            .storageSettingId(storageSetting.getStorageSettingId())
            .dataId(storageSetting.getDataId())
            .targetAmount(storageSetting.getTargetAmount())
            .batchSize(storageSetting.getBatchSize())
            .priority(storageSetting.getPriority())
            .build();
    }

    private List<String> getAvailableResources(List<StorageSetting> storageSettings) {
        List<String> currentResources = storageSettings.stream()
            .map(StorageSetting::getDataId)
            .collect(Collectors.toList());

        return resourceDataService.keySet()
            .stream()
            .filter(dataId -> !currentResources.contains(dataId))
            .collect(Collectors.toList());
    }

    private Map<String, Integer> countAvailableStorage(List<Building> buildings, StorageDetails storageSettings) {
        return Arrays.stream(StorageType.values())
            .collect(Collectors.toMap(Enum::name, storageType -> countAvailableStorage(storageType, buildings, storageSettings)));
    }

    private int countAvailableStorage(StorageType storageType, List<Building> buildings, StorageDetails storageSettings) {
        int usedStorage = countStoredResources(storageType, storageSettings.getStoredResources()) + countReservedStorage(storageType, storageSettings.getReservedStorages());
        int capacity = countCapacity(storageType, buildings);

        return capacity - usedStorage;
    }

    private int countStoredResources(StorageType storageType, Map<String, StoredResource> storedResources) {
        return storedResources.values()
            .stream()
            .filter(storedResource -> resourceDataService.get(storedResource.getDataId()).getStorageType().equals(storageType))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    private int countReservedStorage(StorageType storageType, List<ReservedStorage> reservedStorages) {
        return reservedStorages.stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    private int countCapacity(StorageType storageType, List<Building> buildings) {
        StorageBuilding storageBuilding = storageBuildingService.findByStorageType(storageType);

        return buildings.stream()
            .filter(building -> building.getDataId().equals(storageBuilding.getId()))
            .mapToInt(value -> value.getLevel() * storageBuilding.getCapacity())
            .sum();
    }
}
