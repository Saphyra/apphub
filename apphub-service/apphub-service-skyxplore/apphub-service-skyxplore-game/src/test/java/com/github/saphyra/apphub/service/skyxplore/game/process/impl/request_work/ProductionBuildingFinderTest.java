package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.BuildingCapacityCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductionBuildingFinderTest {
    private static final String DATA_ID = "data-id";
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private BuildingCapacityCalculator buildingCapacityCalculator;

    @InjectMocks
    private ProductionBuildingFinder underTest;

    @Mock
    private Building building;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Test
    public void findSuitableProductionBuilding() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.getByLocationAndDataId(LOCATION, DATA_ID)).willReturn(List.of(building));
        given(buildingCapacityCalculator.calculateCapacity(gameData, building)).willReturn(1);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        Optional<UUID> result = underTest.findSuitableProductionBuilding(gameData, LOCATION, DATA_ID);

        assertThat(result).contains(BUILDING_ID);
    }
}