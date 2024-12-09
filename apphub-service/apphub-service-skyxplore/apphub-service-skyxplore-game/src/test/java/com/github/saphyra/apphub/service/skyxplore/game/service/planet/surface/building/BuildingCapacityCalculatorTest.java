package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
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
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @InjectMocks
    private BuildingCapacityCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private BuildingAllocation buildingAllocation;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Test
    void isAvailable_buildingDeconstructed() {
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));

        assertThat(underTest.isAvailable(gameData, buildingModule)).isFalse();
    }

    @Test
    void isAvailable_allocated() {
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(buildingAllocations.getByBuildingModuleId(BUILDING_MODULE_ID)).willReturn(List.of(buildingAllocation));

        assertThat(underTest.isAvailable(gameData, buildingModule)).isFalse();
    }

    @Test
    void isAvailable_available() {
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(buildingAllocations.getByBuildingModuleId(BUILDING_MODULE_ID)).willReturn(List.of());

        assertThat(underTest.isAvailable(gameData, buildingModule)).isTrue();
    }
}