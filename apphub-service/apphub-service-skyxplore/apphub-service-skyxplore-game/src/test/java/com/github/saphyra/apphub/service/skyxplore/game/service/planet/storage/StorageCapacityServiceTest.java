package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageCapacityServiceTest {
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONTAINER_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 3;
    private static final String BUILDING_MODULE_DATA_ID = "building-module-data-id";
    private static final UUID ALLOCATED_BY = UUID.randomUUID();
    private static final Integer CAPACITY = 32;
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private StorageBuildingModuleDataService storageBuildingModuleDataService;

    @Mock
    private DwellingBuildingDataService dwellingBuildingDataService;

    @Mock
    private BuildingModuleService buildingModuleService;

    @InjectMocks
    private StorageCapacityService underTest;

    @Mock
    private ResourceData resourceData;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private StorageBuildingModuleData storageBuildingModuleData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private DwellingBuildingModuleData dwellingBuildingModuleData;

    @Test
    void getOccupiedDepotStorage() {
        mockDataIdsByStorageType();

        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getOccupiedDepotStorage(gameData, LOCATION, StorageType.CONTAINER)).isEqualTo(AMOUNT);
    }

    @Test
    void getReservedDepotCapacity() {
        mockDataIdsByStorageType();

        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(reservedStorages.getByContainerId(CONTAINER_ID)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(reservedStorage.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getReservedDepotCapacity(gameData, LOCATION, StorageType.CONTAINER)).isEqualTo(AMOUNT);
    }

    @Test
    void getDepotCapacity() {
        given(buildingModuleService.getUsableDepots(gameData, LOCATION, StorageType.CONTAINER)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, AMOUNT));

        assertThat(underTest.getDepotCapacity(gameData, LOCATION, StorageType.CONTAINER)).isEqualTo(AMOUNT);
    }

    @Test
    void getReservedDepotAmount() {
        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(reservedStorages.getByContainerId(CONTAINER_ID)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(reservedStorage.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getReservedDepotAmount(gameData, LOCATION, RESOURCE_DATA_ID)).isEqualTo(AMOUNT);
    }

    @Test
    void getDepotStoredAmount() {
        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getDepotStoredAmount(gameData, LOCATION, RESOURCE_DATA_ID)).isEqualTo(AMOUNT);
    }

    @Test
    void getAllocatedResourceAmount() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByLocationAndDataId(LOCATION, RESOURCE_DATA_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAllocatedBy()).willReturn(ALLOCATED_BY);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getAllocatedResourceAmount(gameData, LOCATION, RESOURCE_DATA_ID)).isEqualTo(AMOUNT);
    }

    @Test
    void getDepotAllocatedResourceAmount() {
        mockDataIdsByStorageType();

        given(buildingModuleService.getDepots(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAllocatedBy()).willReturn(ALLOCATED_BY);
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getDepotAllocatedResourceAmount(gameData, LOCATION, StorageType.CONTAINER)).isEqualTo(AMOUNT);
    }

    @ParameterizedTest
    @EnumSource(value = ContainerType.class, names = {"SURFACE", "CONSTRUCTION_AREA"}, mode = EnumSource.Mode.INCLUDE)
    void getEmptyContainerCapacity_infinite(ContainerType containerType) {
        assertThat(underTest.getEmptyContainerCapacity(gameData, CONTAINER_ID, containerType, RESOURCE_DATA_ID)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void getEmptyContainerCapacity() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.findByIdValidated(CONTAINER_ID)).willReturn(buildingModule);
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));

        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(AMOUNT);
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);

        assertThat(underTest.getEmptyContainerCapacity(gameData, CONTAINER_ID, ContainerType.STORAGE, RESOURCE_DATA_ID)).isEqualTo(CAPACITY - AMOUNT);
    }

    @Test
    void getFreeContainerCapacity() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.findByIdValidated(CONTAINER_ID)).willReturn(buildingModule);
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));

        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);

        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getAmount()).willReturn(AMOUNT);
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);

        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.getByContainerId(CONTAINER_ID)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getAmount()).willReturn(AMOUNT);
        given(reservedStorage.getDataId()).willReturn(RESOURCE_DATA_ID);

        assertThat(underTest.getFreeContainerCapacity(gameData, CONTAINER_ID, StorageType.CONTAINER)).isEqualTo(CAPACITY - AMOUNT - AMOUNT);
    }

    @Test
    void getTotalConstructionAreaCapacity() {
        given(buildingModuleService.getUsableConstructionAreaContainers(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));

        assertThat(underTest.getTotalConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).isEqualTo(CAPACITY);
    }

    @Test
    void getOccupiedConstructionAreaCapacity() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getOccupiedConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).isEqualTo(AMOUNT);
    }

    @Test
    void getEmptyConstructionAreaCapacity() {
        given(buildingModuleService.getUsableConstructionAreaContainers(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));

        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(CONTAINER_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.getByContainerId(CONTAINER_ID)).willReturn(List.of(storedResource));
        given(storedResource.getDataId()).willReturn(RESOURCE_DATA_ID);
        given(resourceDataService.get(RESOURCE_DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.CONTAINER);
        given(storedResource.getAmount()).willReturn(AMOUNT);

        assertThat(underTest.getEmptyConstructionAreaCapacity(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).isEqualTo(CAPACITY - AMOUNT);
    }

    @Test
    void calculateDwellingCapacity() {
        given(buildingModuleService.getUsableDwelling(gameData, LOCATION)).willReturn(Stream.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(dwellingBuildingDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(dwellingBuildingModuleData);
        given(dwellingBuildingModuleData.getCapacity()).willReturn(CAPACITY);

        assertThat(underTest.calculateDwellingCapacity(gameData, LOCATION)).isEqualTo(CAPACITY);
    }

    private void mockDataIdsByStorageType() {
        given(resourceDataService.getByStorageType(StorageType.CONTAINER)).willReturn(List.of(resourceData));
        given(resourceData.getId()).willReturn(RESOURCE_DATA_ID);
    }
}