package com.github.saphyra.apphub.service.skyxplore.game.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageTypeResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetStorageQueryService {
    private final GameDao gameDao;
    private final StorageBuildingService storageBuildingService;
    private final ResourceDataService resourceDataService;

    public PlanetStorageResponse getStorage(UUID userId, UUID planetId) {
        Planet planet = gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId);
        return PlanetStorageResponse.builder()
            .energy(getStorageDetails(planet, StorageType.ENERGY))
            .liquid(getStorageDetails(planet, StorageType.LIQUID))
            .bulk(getStorageDetails(planet, StorageType.BULK))
            .build();
    }

    private StorageTypeResponse getStorageDetails(Planet planet, StorageType storageType) {
        return StorageTypeResponse.builder()
            .capacity(getCapacity(planet, storageType))
            .reservedStorageAmount(getReservedStorageAmount(planet, storageType))
            .actualResourceAmount(getActualAmount(planet, storageType))
            .allocatedResourceAmount(getAllocatedResourceAmount(planet, storageType))
            .build();
    }

    private int getCapacity(Planet planet, StorageType storageType) {
        return planet.getSurfaces()
            .values()
            .stream()
            .filter(surface -> !isNull(surface.getBuilding()))
            .map(Surface::getBuilding)
            .filter(building -> storageBuildingService.containsKey(building.getDataId()))
            .filter(building -> storageBuildingService.get(building.getDataId()).getStores().equals(storageType))
            .mapToInt(building -> building.getLevel() * storageBuildingService.get(building.getDataId()).getCapacity())
            .sum();
    }

    private int getReservedStorageAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    private int getActualAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getStoredResources()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    private int getAllocatedResourceAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getAllocatedResources()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }
}
