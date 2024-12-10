package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingAllocationsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    private final BuildingAllocations underTest = new BuildingAllocations();

    @Mock
    private BuildingAllocation buildingAllocation1;

    @Mock
    private BuildingAllocation buildingAllocation2;

    @Test
    void findByProcessId_found() {
        given(buildingAllocation1.getProcessId()).willReturn(PROCESS_ID);

        underTest.add(buildingAllocation1);

        assertThat(underTest.findByProcessId(PROCESS_ID)).contains(buildingAllocation1);
    }

    @Test
    void findByProcessId_notFound() {
        given(buildingAllocation1.getProcessId()).willReturn(UUID.randomUUID());

        underTest.add(buildingAllocation1);

        assertThat(underTest.findByProcessId(PROCESS_ID)).isEmpty();
    }

    @Test
    void getByBuildingId() {
        given(buildingAllocation1.getBuildingId()).willReturn(BUILDING_ID);
        given(buildingAllocation2.getBuildingId()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(buildingAllocation1, buildingAllocation2));

        assertThat(underTest.getByBuildingModuleId(BUILDING_ID)).containsExactly(buildingAllocation1);
    }
}