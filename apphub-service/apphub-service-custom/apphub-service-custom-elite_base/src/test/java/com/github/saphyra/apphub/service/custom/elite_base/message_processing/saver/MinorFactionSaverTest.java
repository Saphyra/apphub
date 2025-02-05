package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFactionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.FactionState;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.FactionStateEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MinorFactionSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String FACTION_NAME = "faction-name";
    private static final Double INFLUENCE = 34.324;

    @Mock
    private MinorFactionDao minorFactionDao;

    @Mock
    private MinorFactionFactory minorFactionFactory;

    @Mock
    private MinorFactionStateFactory minorFactionStateFactory;

    @InjectMocks
    private MinorFactionSaver underTest;

    @Mock
    private MinorFaction minorFaction;

    @Mock
    private FactionState activeState;

    @Mock
    private FactionState pendingState;

    @Mock
    private FactionState recoveringState;

    @Test
    void save_new() {
        Faction faction = Faction.builder()
            .name(FACTION_NAME)
            .state(FactionStateEnum.BOOM.getValue())
            .influence(INFLUENCE)
            .allegiance(Allegiance.ALLIANCE.getValue())
            .activeStates(new FactionState[]{activeState})
            .pendingStates(new FactionState[]{pendingState})
            .recoveringStates(new FactionState[]{recoveringState})
            .build();

        given(minorFactionDao.findByFactionName(FACTION_NAME)).willReturn(Optional.empty());
        given(minorFactionFactory.create(LAST_UPDATE, FACTION_NAME, FactionStateEnum.BOOM, INFLUENCE, Allegiance.ALLIANCE, List.of(activeState), List.of(pendingState), List.of(recoveringState))).willReturn(minorFaction);
        given(minorFaction.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, new Faction[]{faction})).containsExactly(minorFaction);

        then(minorFaction).should(times(0)).setLastUpdate(any());
        then(minorFaction).should(times(0)).setState(any());
        then(minorFaction).should(times(0)).setInfluence(any());
        then(minorFaction).should(times(0)).setAllegiance(any());
        then(minorFaction).should(times(0)).setActiveStates(any());
        then(minorFaction).should(times(0)).setPendingStates(any());
        then(minorFaction).should(times(0)).setRecoveringStates(any());
        then(minorFactionDao).should().save(minorFaction);
    }

    @Test
    void save_existing() {
        Faction faction = Faction.builder()
            .name(FACTION_NAME)
            .state(FactionStateEnum.BOOM.getValue())
            .influence(INFLUENCE)
            .allegiance(Allegiance.ALLIANCE.getValue())
            .activeStates(new FactionState[]{activeState})
            .pendingStates(new FactionState[]{pendingState})
            .recoveringStates(new FactionState[]{recoveringState})
            .build();

        given(minorFactionDao.findByFactionName(FACTION_NAME)).willReturn(Optional.of(minorFaction));
        given(minorFaction.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, new Faction[]{faction})).containsExactly(minorFaction);

        then(minorFaction).should().setLastUpdate(LAST_UPDATE);
        then(minorFaction).should().setState(FactionStateEnum.BOOM);
        then(minorFaction).should().setInfluence(INFLUENCE);
        then(minorFaction).should().setAllegiance(Allegiance.ALLIANCE);
        then(minorFaction).should().setActiveStates(any());
        then(minorFaction).should().setPendingStates(any());
        then(minorFaction).should().setRecoveringStates(any());
        then(minorFactionDao).should().save(minorFaction);
    }
}