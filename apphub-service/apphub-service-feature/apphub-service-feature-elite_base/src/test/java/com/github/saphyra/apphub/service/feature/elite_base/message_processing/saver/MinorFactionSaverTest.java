package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.feature.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.MinorFactionFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.FactionState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MinorFactionSaverTest {
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final String FACTION_NAME = "faction-name";
    private static final Double INFLUENCE = 34.324;
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();

    @Mock
    private MinorFactionDao minorFactionDao;

    @Mock
    private MinorFactionFactory minorFactionFactory;

    @Mock
    private MinorFactionStateFactory minorFactionStateFactory;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

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

    @Mock
    private LastUpdate lastUpdate;

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
        given(minorFactionFactory.create(FACTION_NAME, FactionStateEnum.BOOM, INFLUENCE, Allegiance.ALLIANCE, List.of(activeState), List.of(pendingState), List.of(recoveringState))).willReturn(minorFaction);
        given(minorFaction.getId()).willReturn(MINOR_FACTION_ID);
        given(lastUpdateFactory.create(MINOR_FACTION_ID, ObjectType.MINOR_FACTION, LAST_UPDATE)).willReturn(lastUpdate);

        assertThat(underTest.save(LAST_UPDATE, new Faction[]{faction})).containsExactly(minorFaction);

        then(minorFaction).should(times(0)).setState(any());
        then(minorFaction).should(times(0)).setInfluence(any());
        then(minorFaction).should(times(0)).setAllegiance(any());
        then(minorFaction).should(times(0)).setActiveStates(any());
        then(minorFaction).should(times(0)).setPendingStates(any());
        then(minorFaction).should(times(0)).setRecoveringStates(any());
        then(minorFactionDao).should().save(minorFaction);
        then(lastUpdateDao).should().save(lastUpdate);
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
        given(minorFaction.getId()).willReturn(MINOR_FACTION_ID);
        given(lastUpdateDao.findByIdOrDefault(MINOR_FACTION_ID, ObjectType.MINOR_FACTION)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));
        given(lastUpdateFactory.create(MINOR_FACTION_ID, ObjectType.MINOR_FACTION, LAST_UPDATE)).willReturn(lastUpdate);

        assertThat(underTest.save(LAST_UPDATE, new Faction[]{faction})).containsExactly(minorFaction);

        then(minorFaction).should().setState(FactionStateEnum.BOOM);
        then(minorFaction).should().setInfluence(INFLUENCE);
        then(minorFaction).should().setAllegiance(Allegiance.ALLIANCE);
        then(minorFaction).should().setActiveStates(any());
        then(minorFaction).should().setPendingStates(any());
        then(minorFaction).should().setRecoveringStates(any());
        then(minorFactionDao).should().save(minorFaction);
        then(lastUpdateDao).should().save(lastUpdate);
    }

    @Test
    void save_outdated() {
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
        given(minorFaction.getId()).willReturn(MINOR_FACTION_ID);
        given(lastUpdateDao.findByIdOrDefault(MINOR_FACTION_ID, ObjectType.MINOR_FACTION)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        assertThat(underTest.save(LAST_UPDATE, new Faction[]{faction})).containsExactly(minorFaction);

        then(minorFactionDao).should(never()).save(any());
        then(lastUpdateDao).should(never()).save(any());
    }
}