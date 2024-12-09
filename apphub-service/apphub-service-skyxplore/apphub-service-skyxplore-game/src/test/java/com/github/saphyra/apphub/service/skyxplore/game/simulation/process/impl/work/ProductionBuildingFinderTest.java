package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.BuildingCapacityCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionBuildingFinderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProductionBuildingFinder underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void findSuitableProductionBuilding() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByLocationAndDataId(LOCATION, BUILDING_DATA_ID)).willReturn(List.of(buildingModule));
        given(buildingCapacityCalculator.isAvailable(gameData, buildingModule)).willReturn(true);
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);

        assertThat(underTest.findSuitableProductionBuilding(gameData, LOCATION, BUILDING_DATA_ID)).contains(BUILDING_MODULE_ID);
    }
}