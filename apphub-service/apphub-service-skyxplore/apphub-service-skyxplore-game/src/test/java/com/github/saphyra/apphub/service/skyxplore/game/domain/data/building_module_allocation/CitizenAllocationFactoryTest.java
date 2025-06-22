package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CitizenAllocationFactoryTest {
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private CitizenAllocationConverter citizenAllocationConverter;

    @InjectMocks
    private CitizenAllocationFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenAllocationModel model;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(CITIZEN_ALLOCATION_ID);

        CitizenAllocation result = underTest.create(CITIZEN_ID, PROCESS_ID);

        assertThat(result.getCitizenAllocationId()).isEqualTo(CITIZEN_ALLOCATION_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(CITIZEN_ALLOCATION_ID);
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(citizenAllocationConverter.toModel(eq(GAME_ID), any(CitizenAllocation.class))).willReturn(model);

        CitizenAllocation result = underTest.save(progressDiff, gameData, CITIZEN_ID, PROCESS_ID);

        then(citizenAllocations).should().add(result);
        then(progressDiff).should().save(model);

        assertThat(result.getCitizenAllocationId()).isEqualTo(CITIZEN_ALLOCATION_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}