package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingAllocationFactoryTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BuildingAllocationFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(BUILDING_ALLOCATION_ID);

        BuildingAllocation result = underTest.create(BUILDING_ID, PROCESS_ID);

        assertThat(result.getBuildingAllocationId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}