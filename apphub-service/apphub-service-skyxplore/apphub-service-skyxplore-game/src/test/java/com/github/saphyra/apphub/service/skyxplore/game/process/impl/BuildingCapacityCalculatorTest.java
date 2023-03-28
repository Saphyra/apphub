package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
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
public class BuildingCapacityCalculatorTest {
    public static final int WORKERS = 3;
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 2;
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingService productionBuildingService;

    @InjectMocks
    private BuildingCapacityCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Building building;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Mock
    private BuildingAllocation buildingAllocation;

    @Test
    public void calculateCapacity() {
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);
        given(productionBuildingService.get(DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getWorkers()).willReturn(WORKERS);
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(buildingAllocations.getByBuildingId(BUILDING_ID)).willReturn(List.of(buildingAllocation, buildingAllocation));

        int result = underTest.calculateCapacity(gameData, building);

        assertThat(result).isEqualTo(4);
    }
}