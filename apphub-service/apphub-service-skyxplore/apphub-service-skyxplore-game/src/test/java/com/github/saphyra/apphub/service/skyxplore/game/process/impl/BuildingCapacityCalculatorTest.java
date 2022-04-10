package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.BuildingAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BuildingCapacityCalculatorTest {
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 2;
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingService productionBuildingService;

    @InjectMocks
    private BuildingCapacityCalculator underTest;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Test
    public void calculateCapacity() {
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);
        given(productionBuildingService.get(DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getWorkers()).willReturn(1);
        given(planet.getBuildingAllocations()).willReturn(buildingAllocations);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(buildingAllocations.get(BUILDING_ID)).willReturn(List.of(UUID.randomUUID()));

        int result = underTest.calculateCapacity(planet, building);

        assertThat(result).isEqualTo(1);
    }
}