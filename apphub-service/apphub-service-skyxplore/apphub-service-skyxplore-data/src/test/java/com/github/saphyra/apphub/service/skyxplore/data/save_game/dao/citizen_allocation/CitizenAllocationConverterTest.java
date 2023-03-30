package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
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
class CitizenAllocationConverterTest {
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String CITIZEN_ALLOCATION_ID_STRING = "citizen-allocation-id";
    private static final String GAME_ID_ID_STRING = "game-id";
    private static final String CITIZEN_ID_ID_STRING = "citizen-id";
    private static final String PROCESS_ID_ID_STRING = "process-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CitizenAllocationConverter underTest;

    @Test
    void convertDomain() {
        CitizenAllocationModel model = new CitizenAllocationModel();
        model.setId(CITIZEN_ALLOCATION_ID);
        model.setGameId(GAME_ID);
        model.setCitizenId(CITIZEN_ID);
        model.setProcessId(PROCESS_ID);

        given(uuidConverter.convertDomain(CITIZEN_ALLOCATION_ID)).willReturn(CITIZEN_ALLOCATION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_ID_STRING);
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_ID_STRING);
        given(uuidConverter.convertDomain(PROCESS_ID)).willReturn(PROCESS_ID_ID_STRING);

        CitizenAllocationEntity result = underTest.convertDomain(model);

        assertThat(result.getCitizenAllocationId()).isEqualTo(CITIZEN_ALLOCATION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_ID_STRING);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID_ID_STRING);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID_ID_STRING);
    }

    @Test
    void convertEntity() {
        CitizenAllocationEntity entity = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_STRING)
            .gameId(GAME_ID_ID_STRING)
            .citizenId(CITIZEN_ID_ID_STRING)
            .processId(PROCESS_ID_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(CITIZEN_ALLOCATION_ID_STRING)).willReturn(CITIZEN_ALLOCATION_ID);
        given(uuidConverter.convertEntity(GAME_ID_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(CITIZEN_ID_ID_STRING)).willReturn(CITIZEN_ID);
        given(uuidConverter.convertEntity(PROCESS_ID_ID_STRING)).willReturn(PROCESS_ID);

        CitizenAllocationModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(CITIZEN_ALLOCATION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CITIZEN_ALLOCATION);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}