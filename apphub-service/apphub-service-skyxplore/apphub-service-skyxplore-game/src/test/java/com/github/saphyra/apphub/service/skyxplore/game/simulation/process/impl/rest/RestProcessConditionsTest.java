package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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
class RestProcessConditionsTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    private final RestProcessConditions underTest = new RestProcessConditions();

    @Mock
    private GameData gameData;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Test
    void citizenAllocated() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));

        assertThat(underTest.citizenAllocated(gameData, CITIZEN_ID)).isTrue();
    }
}