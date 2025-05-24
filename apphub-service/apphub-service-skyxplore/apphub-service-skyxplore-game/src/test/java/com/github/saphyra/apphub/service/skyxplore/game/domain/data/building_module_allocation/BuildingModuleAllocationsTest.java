package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModuleAllocationsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    private final BuildingModuleAllocations underTest = new BuildingModuleAllocations();

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation1;

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation2;

    @Test
    void findByProcessId_found() {
        given(buildingModuleAllocation1.getProcessId()).willReturn(PROCESS_ID);

        underTest.add(buildingModuleAllocation1);

        assertThat(underTest.findByProcessId(PROCESS_ID)).contains(buildingModuleAllocation1);
    }

    @Test
    void findByProcessId_notFound() {
        given(buildingModuleAllocation1.getProcessId()).willReturn(UUID.randomUUID());

        underTest.add(buildingModuleAllocation1);

        assertThat(underTest.findByProcessId(PROCESS_ID)).isEmpty();
    }

    @Test
    void getByBuildingId() {
        given(buildingModuleAllocation1.getBuildingModuleId()).willReturn(BUILDING_ID);
        given(buildingModuleAllocation2.getBuildingModuleId()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(buildingModuleAllocation1, buildingModuleAllocation2));

        assertThat(underTest.getByBuildingModuleId(BUILDING_ID)).containsExactly(buildingModuleAllocation1);
    }
}