package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CitizenAllocationFactoryTest {
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CitizenAllocationFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(CITIZEN_ALLOCATION_ID);

        CitizenAllocation result = underTest.create(CITIZEN_ID, PROCESS_ID);

        assertThat(result.getCitizenAllocationId()).isEqualTo(CITIZEN_ALLOCATION_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}