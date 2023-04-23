package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
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
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProductionBuildingFinder underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Test
    void findSuitableProductionBuilding() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.getByLocationAndDataId(LOCATION, BUILDING_DATA_ID)).willReturn(List.of(building));
        given(buildingCapacityCalculator.calculateCapacity(gameData, building)).willReturn(1);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        assertThat(underTest.findSuitableProductionBuilding(gameData, LOCATION, BUILDING_DATA_ID)).contains(BUILDING_ID);
    }
}