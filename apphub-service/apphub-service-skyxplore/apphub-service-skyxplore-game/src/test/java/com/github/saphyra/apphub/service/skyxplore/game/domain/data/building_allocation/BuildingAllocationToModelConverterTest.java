package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingAllocationToModelConverterTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    private final BuildingAllocationToModelConverter underTest = new BuildingAllocationToModelConverter();

    @Test
    void convert() {
        BuildingAllocation buildingAllocation = BuildingAllocation.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID)
            .buildingId(BUILDING_ID)
            .processId(PROCESS_ID)
            .build();

        BuildingAllocationModel result = underTest.convert(GAME_ID, buildingAllocation);

        assertThat(result.getId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.BUILDING_ALLOCATION);
        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}