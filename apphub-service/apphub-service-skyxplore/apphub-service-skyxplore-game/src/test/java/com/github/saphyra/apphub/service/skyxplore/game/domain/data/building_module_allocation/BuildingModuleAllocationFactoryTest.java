package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BuildingModuleAllocationFactoryTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private BuildingModuleAllocationConverter buildingModuleAllocationConverter;

    @InjectMocks
    private BuildingModuleAllocationFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModuleAllocationModel model;

    @Mock
    private BuildingModuleAllocations buildingModuleAllocations;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(BUILDING_ALLOCATION_ID);

        BuildingModuleAllocation result = underTest.create(BUILDING_ID, PROCESS_ID);

        assertThat(result.getBuildingModuleAllocationId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getBuildingModuleId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(BUILDING_ALLOCATION_ID);
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocationConverter.toModel(ArgumentMatchers.eq(GAME_ID), any(BuildingModuleAllocation.class))).willReturn(model);
        given(gameData.getGameId()).willReturn(GAME_ID);

        BuildingModuleAllocation result = underTest.save(progressDiff, gameData, BUILDING_ID, PROCESS_ID);

        then(buildingModuleAllocations).should().add(result);
        then(progressDiff).should().save(model);

        assertThat(result.getBuildingModuleAllocationId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getBuildingModuleId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}