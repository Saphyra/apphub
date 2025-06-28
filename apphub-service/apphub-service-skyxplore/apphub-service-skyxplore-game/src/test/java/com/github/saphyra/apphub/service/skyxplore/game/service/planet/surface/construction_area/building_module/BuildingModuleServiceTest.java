package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production.ProductionBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.Production;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModuleServiceTest {
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String BUILDING_MODULE_DATA_ID = "building-module-data-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    @Mock
    private StorageBuildingModuleDataService storageBuildingModuleDataService;

    @Mock
    private DwellingBuildingDataService dwellingBuildingDataService;

    @Mock
    private ProductionBuildingModuleDataService productionBuildingModuleDataService;

    @InjectMocks
    private BuildingModuleService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private StorageBuildingModuleData storageBuildingModuleData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Production production;

    @Mock
    private ProductionBuildingModuleData productionBuildingModuleData;

    @Test
    void getUsableConstructionAreaContainers() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.containsKey(BUILDING_MODULE_DATA_ID)).willReturn(true);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, 0));

        assertThat(underTest.getUsableConstructionAreaContainers(gameData, CONSTRUCTION_AREA_ID, StorageType.CONTAINER)).containsExactly(buildingModule);
    }

    @Test
    void getUsableDepots() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.containsKey(BUILDING_MODULE_DATA_ID)).willReturn(true);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getCategory()).willReturn(BuildingModuleCategory.LARGE_STORAGE);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, 0));

        assertThat(underTest.getUsableDepots(gameData, LOCATION, StorageType.CONTAINER)).containsExactly(buildingModule);
    }

    @Test
    void getUsableDwelling() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(dwellingBuildingDataService.containsKey(BUILDING_MODULE_DATA_ID)).willReturn(true);

        assertThat(underTest.getUsableDwelling(gameData, LOCATION)).containsExactly(buildingModule);
    }

    @Test
    void getProducersOf() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(productionBuildingModuleDataService.containsKey(BUILDING_MODULE_DATA_ID)).willReturn(true);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(productionBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(productionBuildingModuleData);
        given(productionBuildingModuleData.getProduces()).willReturn(List.of(production));
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);

        assertThat(underTest.getProducersOf(gameData, LOCATION, RESOURCE_DATA_ID)).containsExactly(buildingModule);
    }

    @Test
    void getAvailableStorageTypes(){
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(storageBuildingModuleDataService.containsKey(BUILDING_MODULE_DATA_ID)).willReturn(true);
        given(storageBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(storageBuildingModuleData);
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, 0));

        assertThat(underTest.getAvailableStorageTypes(gameData, CONSTRUCTION_AREA_ID)).containsExactly(StorageType.CONTAINER);
    }

    @Test
    void getUsableConstructionAreaProducers(){
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);
        given(productionBuildingModuleDataService.containsKey(BUILDING_MODULE_DATA_ID)).willReturn(true);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(productionBuildingModuleDataService.get(BUILDING_MODULE_DATA_ID)).willReturn(productionBuildingModuleData);
        given(productionBuildingModuleData.getProduces()).willReturn(List.of(production));
        given(production.getResourceDataId()).willReturn(RESOURCE_DATA_ID);

        assertThat(underTest.getUsableConstructionAreaProducers(gameData, CONSTRUCTION_AREA_ID, RESOURCE_DATA_ID)).containsExactly(buildingModule);
    }
}