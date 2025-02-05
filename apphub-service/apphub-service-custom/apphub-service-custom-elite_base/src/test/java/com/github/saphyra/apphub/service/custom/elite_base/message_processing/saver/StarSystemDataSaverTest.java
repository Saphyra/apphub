package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.StarSystemDataFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.EdConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.SecurityLevel;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.ConflictMapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.MinorFactionIdResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StarSystemDataSaverTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long POPULATION = 242L;
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();
    private static final String CONTROLLING_FACTION_NAME = "controlling-faction-name";
    private static final UUID CONTROLLING_FACTION_ID = UUID.randomUUID();

    @Mock
    private StarSystemDataDao starSystemDataDao;

    @Mock
    private StarSystemDataFactory starSystemDataFactory;

    @Mock
    private ConflictMapper conflictMapper;

    @Mock
    private MinorFactionIdResolver minorFactionIdResolver;

    @InjectMocks
    private StarSystemDataSaver underTest;

    @Mock
    private MinorFaction minorFaction;

    @Mock
    private EdConflict edConflict;

    @Mock
    private MinorFactionConflict minorFactionConflict;

    @Mock
    private StarSystemData starSystemData;

    @Mock
    private ControllingFaction controllingFaction;

    @Test
    void save_new() {
        EdConflict[] conflicts = {edConflict};
        given(conflictMapper.mapConflicts(LAST_UPDATE, STAR_SYSTEM_ID, conflicts, List.of(minorFaction))).willReturn(List.of(minorFactionConflict));
        given(starSystemDataDao.findById(STAR_SYSTEM_ID)).willReturn(Optional.empty());
        given(starSystemDataFactory.create(
            STAR_SYSTEM_ID,
            LAST_UPDATE,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            List.of(minorFactionConflict)
        ))
            .willReturn(starSystemData);
        given(starSystemData.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        underTest.save(
            STAR_SYSTEM_ID,
            LAST_UPDATE,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );

        then(starSystemData).should(times(0)).setLastUpdate(any());
        then(starSystemData).should(times(0)).setPopulation(any());
        then(starSystemData).should(times(0)).setAllegiance(any());
        then(starSystemData).should(times(0)).setEconomy(any());
        then(starSystemData).should(times(0)).setSecondaryEconomy(any());
        then(starSystemData).should(times(0)).setSecurityLevel(any());
        then(starSystemData).should(times(0)).setControllingPower(any());
        then(starSystemData).should(times(0)).setPowerplayState(any());
        then(starSystemData).should(times(0)).setMinorFactions(any());
        then(starSystemData).should(times(0)).setControllingFactionId(any());
        then(starSystemData).should(times(0)).setControllingFactionState(any());
        then(starSystemData).should(times(0)).setPowers(any());
        then(starSystemData).should(times(0)).setConflicts(any());
        then(starSystemDataDao).should().save(starSystemData);
    }

    @Test
    void save() {
        EdConflict[] conflicts = {edConflict};
        given(conflictMapper.mapConflicts(LAST_UPDATE, STAR_SYSTEM_ID, conflicts, List.of(minorFaction))).willReturn(List.of(minorFactionConflict));
        given(starSystemDataDao.findById(STAR_SYSTEM_ID)).willReturn(Optional.of(starSystemData));
        given(starSystemData.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));
        given(minorFaction.getId()).willReturn(MINOR_FACTION_ID);
        given(controllingFaction.getFactionName()).willReturn(CONTROLLING_FACTION_NAME);
        given(controllingFaction.getState()).willReturn(FactionStateEnum.DROUGHT.name());
        given(minorFactionIdResolver.getMinorFactionId(LAST_UPDATE, CONTROLLING_FACTION_NAME, List.of(minorFaction))).willReturn(CONTROLLING_FACTION_ID);

        underTest.save(
            STAR_SYSTEM_ID,
            LAST_UPDATE,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            List.of(minorFaction),
            controllingFaction,
            List.of(Power.NAKATO_KAINE),
            conflicts
        );

        then(starSystemData).should().setLastUpdate(LAST_UPDATE);
        then(starSystemData).should().setPopulation(POPULATION);
        then(starSystemData).should().setAllegiance(Allegiance.ALLIANCE);
        then(starSystemData).should().setEconomy(EconomyEnum.AGRICULTURE);
        then(starSystemData).should().setSecondaryEconomy(EconomyEnum.COLONY);
        then(starSystemData).should().setSecurityLevel(SecurityLevel.ANARCHY);
        then(starSystemData).should().setControllingPower(Power.AISLING_DUVAL);
        then(starSystemData).should().setPowerplayState(PowerplayState.FORTIFIED);
        then(starSystemData).should().setMinorFactions(any());
        then(starSystemData).should().setControllingFactionId(CONTROLLING_FACTION_ID);
        then(starSystemData).should().setControllingFactionState(FactionStateEnum.DROUGHT);
        then(starSystemData).should().setPowers(any());
        then(starSystemData).should().setConflicts(any());
        then(starSystemDataDao).should().save(starSystemData);
    }
}