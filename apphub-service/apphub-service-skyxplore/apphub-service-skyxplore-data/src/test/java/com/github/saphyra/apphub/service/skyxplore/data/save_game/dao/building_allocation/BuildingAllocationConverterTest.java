package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingAllocationConverterTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String BUILDING_ALLOCATION_ID_STRING = "building-allocation-id";
    private static final String GAME_ID_ID_STRING = "game-id";
    private static final String BUILDING_ID_ID_STRING = "building-id";
    private static final String PROCESS_ID_ID_STRING = "process-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BuildingAllocationConverter underTest;

    @Test
    void convertDomain() {
        BuildingModuleAllocationModel model = new BuildingModuleAllocationModel();
        model.setId(BUILDING_ALLOCATION_ID);
        model.setGameId(GAME_ID);
        model.setBuildingModuleId(BUILDING_ID);
        model.setProcessId(PROCESS_ID);

        given(uuidConverter.convertDomain(BUILDING_ALLOCATION_ID)).willReturn(BUILDING_ALLOCATION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_ID_STRING);
        given(uuidConverter.convertDomain(BUILDING_ID)).willReturn(BUILDING_ID_ID_STRING);
        given(uuidConverter.convertDomain(PROCESS_ID)).willReturn(PROCESS_ID_ID_STRING);

        BuildingModuleAllocationEntity result = underTest.convertDomain(model);

        assertThat(result.getBuildingAllocationId()).isEqualTo(BUILDING_ALLOCATION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_ID_STRING);
        assertThat(result.getBuildingModuleId()).isEqualTo(BUILDING_ID_ID_STRING);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID_ID_STRING);
    }

    @Test
    void convertEntity() {
        BuildingModuleAllocationEntity entity = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_STRING)
            .gameId(GAME_ID_ID_STRING)
            .buildingModuleId(BUILDING_ID_ID_STRING)
            .processId(PROCESS_ID_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(BUILDING_ALLOCATION_ID_STRING)).willReturn(BUILDING_ALLOCATION_ID);
        given(uuidConverter.convertEntity(GAME_ID_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(BUILDING_ID_ID_STRING)).willReturn(BUILDING_ID);
        given(uuidConverter.convertEntity(PROCESS_ID_ID_STRING)).willReturn(PROCESS_ID);

        BuildingModuleAllocationModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CITIZEN_ALLOCATION);
        assertThat(result.getBuildingModuleId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}