package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
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
class BuildingCapacityCalculatorTest {
    private static final String DATA_ID = "data-id";
    private static final Integer WORKERS = 34;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Integer LEVEL = 23;

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private HeadquartersUtil headquartersUtil;

    @InjectMocks
    private BuildingCapacityCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Building building;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private ProductionBuildingData productionBuildingData;

    @Mock
    private BuildingAllocation buildingAllocation;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Test
    void calculateCapacity_buildingDeconstructed() {
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(deconstruction));

        assertThat(underTest.calculateCapacity(gameData, building)).isEqualTo(0);
    }

    @Test
    void calculateCapacity() {
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(building.getDataId()).willReturn(DATA_ID);
        given(productionBuildingService.get(DATA_ID)).willReturn(productionBuildingData);
        given(productionBuildingData.getWorkers()).willReturn(WORKERS);
        given(building.getLevel()).willReturn(LEVEL);
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(buildingAllocations.getByBuildingId(BUILDING_ID)).willReturn(List.of(buildingAllocation));

        assertThat(underTest.calculateCapacity(gameData, building)).isEqualTo(LEVEL * WORKERS - 1);
    }

    @Test
    void calculateCapacity_hq() {
        given(building.getBuildingId()).willReturn(BUILDING_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.empty());
        given(building.getDataId()).willReturn(DATA_ID);
        given(productionBuildingService.get(DATA_ID)).willReturn(null);
        given(headquartersUtil.getWorkers()).willReturn(WORKERS);
        given(building.getLevel()).willReturn(LEVEL);
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(buildingAllocations.getByBuildingId(BUILDING_ID)).willReturn(List.of(buildingAllocation));

        assertThat(underTest.calculateCapacity(gameData, building)).isEqualTo(LEVEL * WORKERS - 1);
    }
}