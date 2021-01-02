package com.github.saphyra.apphub.service.skyxplore.game.service.planet.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageTypeResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        log.debug("PlanetStorage: {}", planet.getStorageDetails());
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
            .resourceDetails(getResourceDetails(planet, storageType))
            .build();
    }

    public int getFreeStorage(Planet planet, StorageType storageType) {
        return getCapacity(planet, storageType) - getActualAmount(planet, storageType) - getReservedStorageAmount(planet, storageType);
    }

    public int getCapacity(Planet planet, StorageType storageType) {
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

    public int getReservedStorageAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(ReservedStorage::getAmount)
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

    public int getAllocatedResourceAmount(Planet planet, StorageType storageType) {
        return planet.getStorageDetails()
            .getAllocatedResources()
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType().equals(storageType))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }

    private List<ResourceDetailsResponse> getResourceDetails(Planet planet, StorageType storageType) {
        return resourceDataService.getByStorageType(storageType)
            .stream()
            .map(resourceData -> createResourceData(resourceData, planet.getStorageDetails()))
            .filter(ResourceDetailsResponse::valuePresent)
            .collect(Collectors.toList());
    }

    private ResourceDetailsResponse createResourceData(ResourceData resourceData, StorageDetails storageDetails) {
        return ResourceDetailsResponse.builder()
            .dataId(resourceData.getId())
            .reservedStorageAmount(getReservedStorageAmount(resourceData.getId(), storageDetails.getReservedStorages()))
            .actualAmount(getActualAmount(resourceData.getId(), storageDetails.getStoredResources()))
            .allocatedResourceAmount(getAllocatedResourceAmount(resourceData.getId(), storageDetails.getAllocatedResources()))
            .build();
    }

    public int getReservedStorageAmount(String dataId, Planet planet) {
        return getReservedStorageAmount(dataId, planet.getStorageDetails().getReservedStorages());
    }

    private int getReservedStorageAmount(String dataId, List<ReservedStorage> reservedStorages) {
        return reservedStorages.stream()
            .filter(reservedStorage -> reservedStorage.getDataId().equals(dataId))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    public int getActualAmount(String dataId, Planet planet) {
        return getActualAmount(dataId, planet.getStorageDetails().getStoredResources());
    }

    private int getActualAmount(String dataId, List<StoredResource> storedResources) {
        return storedResources.stream()
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getAllocatedResourceAmount(String dataId, Planet planet) {
        return getAllocatedResourceAmount(dataId, planet.getStorageDetails().getAllocatedResources());
    }

    private int getAllocatedResourceAmount(String dataId, List<AllocatedResource> allocatedResources) {
        return allocatedResources.stream()
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }

    public int getUsableStoredResourceAmount(String dataId, Planet planet) {
        return getActualAmount(dataId, planet) - getAllocatedResourceAmount(dataId, planet);
    }
}
