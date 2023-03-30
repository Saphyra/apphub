package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CitizenAllocationToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    private final CitizenAllocationToModelConverter underTest = new CitizenAllocationToModelConverter();

    @Test
    void convert() {
        CitizenAllocation citizenAllocation = CitizenAllocation.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID)
            .citizenId(CITIZEN_ID)
            .processId(PROCESS_ID)
            .build();

        CitizenAllocationModel result = underTest.convert(GAME_ID, citizenAllocation);

        assertThat(result.getId()).isEqualTo(CITIZEN_ALLOCATION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CITIZEN_ALLOCATION);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}