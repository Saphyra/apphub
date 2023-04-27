package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WorkProcessConditionsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();

    private final WorkProcessConditions underTest = new WorkProcessConditions();

    @Mock
    private GameData gameData;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Mock
    private BuildingAllocation buildingAllocation;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Test
    void buildingAllocated() {
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);
        given(buildingAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(buildingAllocation));

        assertThat(underTest.hasBuildingAllocated(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void hasCitizenAllocated() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(citizenAllocation));

        assertThat(underTest.hasCitizenAllocated(gameData, PROCESS_ID)).isTrue();
    }
}