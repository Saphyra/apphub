package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingModuleAllocationConverterTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    private final BuildingAllocationConverter underTest = new BuildingAllocationConverter();

    @Test
    void convert() {
        BuildingModuleAllocation buildingModuleAllocation = BuildingModuleAllocation.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID)
            .buildingModuleId(BUILDING_ID)
            .processId(PROCESS_ID)
            .build();

        BuildingModuleAllocationModel result = underTest.toModel(GAME_ID, buildingModuleAllocation);

        assertThat(result.getId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.BUILDING_ALLOCATION);
        assertThat(result.getBuildingModuleId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}