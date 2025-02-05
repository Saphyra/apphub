package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state.MinorFactionState;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.FactionState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MinorFactionFactoryTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String FACTION_NAME = "faction-name";
    private static final Double INFLUENCE = 234.34;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private MinorFactionStateFactory minorFactionStateFactory;

    @InjectMocks
    private MinorFactionFactory underTest;

    @Mock
    private FactionState activeFactionState;

    @Mock
    private FactionState pendingFactionState;

    @Mock
    private FactionState recoveringFactionState;

    @Mock
    private MinorFactionState activeState;

    @Mock
    private MinorFactionState pendingState;

    @Mock
    private MinorFactionState recoveringState;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);
        given(minorFactionStateFactory.create(ID, StateStatus.ACTIVE, List.of(activeFactionState))).willReturn(List.of(activeState));
        given(minorFactionStateFactory.create(ID, StateStatus.PENDING, List.of(pendingFactionState))).willReturn(List.of(pendingState));
        given(minorFactionStateFactory.create(ID, StateStatus.RECOVERING, List.of(recoveringFactionState))).willReturn(List.of(recoveringState));

        assertThat(underTest.create(LAST_UPDATE, FACTION_NAME, FactionStateEnum.BLIGHT, INFLUENCE, Allegiance.ALLIANCE, List.of(activeFactionState), List.of(pendingFactionState), List.of(recoveringFactionState)))
            .returns(ID, MinorFaction::getId)
            .returns(LAST_UPDATE, MinorFaction::getLastUpdate)
            .returns(FACTION_NAME, MinorFaction::getFactionName)
            .returns(FactionStateEnum.BLIGHT, MinorFaction::getState)
            .returns(INFLUENCE, MinorFaction::getInfluence)
            .returns(Allegiance.ALLIANCE, MinorFaction::getAllegiance)
            .returns(List.of(activeState), MinorFaction::getActiveStates)
            .returns(List.of(pendingState), MinorFaction::getPendingStates)
            .returns(List.of(recoveringState), MinorFaction::getRecoveringStates);
    }
}