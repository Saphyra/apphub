package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
/*
  Keywords:
  Total = Maximum capacity of all the storage buildings combined
  OccupiedStorage = Storage filled by resources
  StoredAmount = Amount of a specific resource stored
  Reserved = Empty storage assigned to a process that will fill it with resources (delivery, production, etc)
  Free = Not occupied or reserved storage
  Empty = Not occupied, reservations ignored
  Allocated = Stored resources waiting to be picked up / consumed by a process
 */
public class StorageCapacityService {
    private static final List<ContainerType> INFINITE_CONTAINERS = List.of(ContainerType.SURFACE, ContainerType.CONSTRUCTION_AREA);

    private final ResourceDataService resourceDataService;
    private final StorageBuildingModuleDataService storageBuildingModuleDataService;
    private final DwellingBuildingDataService dwellingBuildingDataService;
    private final BuildingModuleService buildingModuleService;

    public int getOccupiedDepotStorage(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .toList();

        return buildingModuleService.getDepots(gameData, location)
            .flatMap(buildingModule -> gameData.getStoredResources().getByContainerId(buildingModule.getBuildingModuleId()).stream())
            .filter(storedResource -> dataIdsByStorageType.contains(storedResource.getDataId()))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getReservedDepotCapacity(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .toList();

        return buildingModuleService.getDepots(gameData, location)
            .flatMap(buildingModule -> gameData.getReservedStorages().getByContainerId(buildingModule.getBuildingModuleId()).stream())
            .filter(storedResource -> dataIdsByStorageType.contains(storedResource.getDataId()))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    public int getDepotCapacity(GameData gameData, UUID location, StorageType storageType) {
        return buildingModuleService.getUsableDepots(gameData, location, storageType)
            .map(buildingModule -> storageBuildingModuleDataService.get(buildingModule.getDataId()))
            .map(StorageBuildingModuleData::getStores)
            .mapToInt(stores -> stores.get(storageType))
            .sum();
    }

    public int getReservedDepotAmount(GameData gameData, UUID location, String dataId) {
        return buildingModuleService.getDepots(gameData, location)
            .flatMap(buildingModule -> gameData.getReservedStorages().getByContainerId(buildingModule.getBuildingModuleId()).stream())
            .filter(reservedStorage -> reservedStorage.getDataId().equals(dataId))
            .mapToInt(ReservedStorage::getAmount)
            .sum();
    }

    public int getDepotStoredAmount(GameData gameData, UUID location, String dataId) {
        return buildingModuleService.getDepots(gameData, location)
            .flatMap(buildingModule -> gameData.getStoredResources().getByContainerId(buildingModule.getBuildingModuleId()).stream())
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getAllocatedResourceAmount(GameData gameData, UUID location, String dataId) {
        return gameData.getStoredResources()
            .getByLocationAndDataId(location, dataId)
            .stream()
            .filter(storedResource -> nonNull(storedResource.getAllocatedBy()))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getDepotAllocatedResourceAmount(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .toList();

        return buildingModuleService.getDepots(gameData, location)
            .map(BuildingModule::getBuildingModuleId)
            .flatMap(buildingModuleId -> gameData.getStoredResources().getByContainerId(buildingModuleId).stream())
            .filter(storedResource -> nonNull(storedResource.getAllocatedBy()))
            .filter(storedResource -> dataIdsByStorageType.contains(storedResource.getDataId()))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getEmptyContainerCapacity(GameData gameData, UUID containerId, ContainerType containerType, String resourceDataId) {
        if (INFINITE_CONTAINERS.contains(containerType)) {
            return Integer.MAX_VALUE;
        }

        String buildingModuleDataId = gameData.getBuildingModules()
            .findByIdValidated(containerId)
            .getDataId();
        StorageBuildingModuleData data = storageBuildingModuleDataService.get(buildingModuleDataId);
        StorageType storageType = resourceDataService.get(resourceDataId)
            .getStorageType();
        int capacity = data.getStores()
            .get(storageType);
        int usedCapacity = gameData.getStoredResources()
            .getByContainerId(containerId)
            .stream()
            .filter(storedResource -> resourceDataService.get(storedResource.getDataId()).getStorageType() == storageType)
            .mapToInt(StoredResource::getAmount)
            .sum();

        return capacity - usedCapacity;
    }

    public int getFreeContainerCapacity(GameData gameData, UUID containerId, StorageType storageType) {
        String buildingModuleDataId = gameData.getBuildingModules()
            .findByIdValidated(containerId)
            .getDataId();
        StorageBuildingModuleData data = storageBuildingModuleDataService.get(buildingModuleDataId);
        int capacity = data.getStores()
            .get(storageType);

        int usedCapacity = gameData.getStoredResources()
            .getByContainerId(containerId)
            .stream()
            .filter(storedResource -> resourceDataService.get(storedResource.getDataId()).getStorageType() == storageType)
            .mapToInt(StoredResource::getAmount)
            .sum();

        int reservedStorageAmount = gameData.getReservedStorages()
            .getByContainerId(containerId)
            .stream()
            .filter(reservedStorage -> resourceDataService.get(reservedStorage.getDataId()).getStorageType() == storageType)
            .mapToInt(ReservedStorage::getAmount)
            .sum();

        return capacity - usedCapacity - reservedStorageAmount;
    }

    public int getTotalConstructionAreaCapacity(GameData gameData, UUID constructionAreaId, StorageType storageType) {
        return buildingModuleService.getUsableConstructionAreaContainers(gameData, constructionAreaId, storageType)
            .map(buildingModule -> storageBuildingModuleDataService.get(buildingModule.getDataId()))
            .mapToInt(buildingModuleData -> buildingModuleData.getStores().get(storageType))
            .sum();
    }

    public int getOccupiedConstructionAreaCapacity(GameData gameData, UUID constructionAreaId, StorageType storageType) {
        return gameData.getBuildingModules()
            .getByConstructionAreaId(constructionAreaId)
            .stream()
            .map(BuildingModule::getBuildingModuleId)
            .flatMap(buildingModuleId -> gameData.getStoredResources().getByContainerId(buildingModuleId).stream())
            .filter(storedResource -> resourceDataService.get(storedResource.getDataId()).getStorageType() == storageType)
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int getEmptyConstructionAreaCapacity(GameData gameData, UUID constructionAreaId, StorageType storageType) {
        int totalCapacity = getTotalConstructionAreaCapacity(gameData, constructionAreaId, storageType);
        int occupied = getOccupiedConstructionAreaCapacity(gameData, constructionAreaId, storageType);

        return totalCapacity - occupied;
    }

    public int calculateDwellingCapacity(GameData gameData, UUID location) {
        return buildingModuleService.getUsableDwelling(gameData, location)
            .mapToInt(buildingModule -> dwellingBuildingDataService.get(buildingModule.getDataId()).getCapacity())
            .sum();
    }
}
