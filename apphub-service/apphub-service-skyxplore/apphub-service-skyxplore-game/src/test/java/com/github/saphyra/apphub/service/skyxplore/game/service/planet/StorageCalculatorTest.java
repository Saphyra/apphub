package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.dwelling.DwellingBuildingDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
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
class StorageCalculatorTest {
    private static final int CAPACITY = 32;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NOT_STORAGE_DATA_ID = "not-storage-data-id";
    private static final String DATA_ID = "data-id";
    private static final UUID DECONSTRUCTED_BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String INCOMPATIBLE_DATA_ID = "incompatible-data-id";

    @Mock
    private StorageBuildingModuleDataService storageBuildingModuleDataService;

    @Mock
    private DwellingBuildingDataService dwellingBuildingDataService;

    @InjectMocks
    private StorageCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule notStorageModule;

    @Mock
    private BuildingModule deconstructedModule;

    @Mock
    private BuildingModule incompatibleModule;

    @Mock
    private BuildingModule matchingModule;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private StorageBuildingModuleData incompatibleStorageBuildingModuleData;

    @Mock
    private StorageBuildingModuleData storageBuildingModuleData;

    @Mock
    private DwellingBuildingModuleData dwellingBuildingModuleData;

    @Test
    void calculateStorageCapacity() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(notStorageModule, deconstructedModule, incompatibleModule, matchingModule, matchingModule));
        given(notStorageModule.getDataId()).willReturn(NOT_STORAGE_DATA_ID);
        given(deconstructedModule.getDataId()).willReturn(DATA_ID);
        given(incompatibleModule.getDataId()).willReturn(INCOMPATIBLE_DATA_ID);
        given(matchingModule.getDataId()).willReturn(DATA_ID);
        given(storageBuildingModuleDataService.containsKey(NOT_STORAGE_DATA_ID)).willReturn(false);
        given(storageBuildingModuleDataService.containsKey(DATA_ID)).willReturn(true);
        given(storageBuildingModuleDataService.containsKey(INCOMPATIBLE_DATA_ID)).willReturn(true);
        given(deconstructedModule.getBuildingModuleId()).willReturn(DECONSTRUCTED_BUILDING_MODULE_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(DECONSTRUCTED_BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));
        given(storageBuildingModuleDataService.get(INCOMPATIBLE_DATA_ID)).willReturn(incompatibleStorageBuildingModuleData);
        given(storageBuildingModuleDataService.get(DATA_ID)).willReturn(storageBuildingModuleData);
        given(incompatibleStorageBuildingModuleData.getStores()).willReturn(Map.of());
        given(storageBuildingModuleData.getStores()).willReturn(Map.of(StorageType.CONTAINER, CAPACITY));

        assertThat(underTest.calculateDepotCapacity(gameData, LOCATION, StorageType.CONTAINER)).isEqualTo(CAPACITY * 2);
    }

    @Test
    void calculateDwellingCapacity() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocation(LOCATION)).willReturn(List.of(incompatibleModule, deconstructedModule, matchingModule, matchingModule));
        given(incompatibleModule.getDataId()).willReturn(INCOMPATIBLE_DATA_ID);
        given(deconstructedModule.getDataId()).willReturn(DATA_ID);
        given(matchingModule.getDataId()).willReturn(DATA_ID);
        given(dwellingBuildingDataService.containsKey(INCOMPATIBLE_DATA_ID)).willReturn(false);
        given(dwellingBuildingDataService.containsKey(DATA_ID)).willReturn(true);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructedModule.getBuildingModuleId()).willReturn(DECONSTRUCTED_BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(DECONSTRUCTED_BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));
        given(dwellingBuildingDataService.get(DATA_ID)).willReturn(dwellingBuildingModuleData);
        given(dwellingBuildingModuleData.getCapacity()).willReturn(CAPACITY);

        assertThat(underTest.calculateDwellingCapacity(gameData, LOCATION)).isEqualTo(CAPACITY * 2);
    }
}