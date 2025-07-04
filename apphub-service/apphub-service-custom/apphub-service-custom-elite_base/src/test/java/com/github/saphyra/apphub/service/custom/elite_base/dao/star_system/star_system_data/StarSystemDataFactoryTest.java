package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data;

import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.SecurityLevel;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemDataFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
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
class StarSystemDataFactoryTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long POPULATION = 32432L;
    private static final String FACTION_NAME = "faction-name";
    private static final UUID CONTROLLING_FACTION_ID = UUID.randomUUID();
    private static final Double POWERPLAY_STATE_CONTROL_PROGRESS = 313d;
    private static final Double POWERPLAY_STATE_REINFORCEMENT = 13d;
    private static final Double POWERPLAY_STATE_UNDERMINING = 54d;

    @Mock
    private MinorFactionSaver minorFactionSaver;

    @InjectMocks
    private StarSystemDataFactory underTest;

    @Mock
    private MinorFaction minorFaction;

    @Mock
    private ControllingFaction controllingFaction;

    @Mock
    private MinorFactionConflict minorFactionConflict;

    @Mock
    private MinorFaction newMinorFaction;

    @Mock
    private PowerplayConflict powerplayConflict;

    @Test
    void controllingFactionIsInFactionList() {
        given(minorFaction.getFactionName()).willReturn(FACTION_NAME);
        given(minorFaction.getId()).willReturn(CONTROLLING_FACTION_ID);
        given(controllingFaction.getFactionName()).willReturn(FACTION_NAME);
        given(controllingFaction.getState()).willReturn("Retreat");

        assertThat(underTest.create(
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
            List.of(minorFactionConflict),
            POWERPLAY_STATE_CONTROL_PROGRESS,
            POWERPLAY_STATE_REINFORCEMENT,
            POWERPLAY_STATE_UNDERMINING,
            List.of(powerplayConflict)
        ))
            .returns(STAR_SYSTEM_ID, StarSystemData::getStarSystemId)
            .returns(LAST_UPDATE, StarSystemData::getLastUpdate)
            .returns(POPULATION, StarSystemData::getPopulation)
            .returns(Allegiance.ALLIANCE, StarSystemData::getAllegiance)
            .returns(EconomyEnum.AGRICULTURE, StarSystemData::getEconomy)
            .returns(EconomyEnum.COLONY, StarSystemData::getSecondaryEconomy)
            .returns(SecurityLevel.ANARCHY, StarSystemData::getSecurityLevel)
            .returns(Power.AISLING_DUVAL, StarSystemData::getControllingPower)
            .returns(PowerplayState.FORTIFIED, StarSystemData::getPowerplayState)
            .returns(CONTROLLING_FACTION_ID, StarSystemData::getControllingFactionId)
            .returns(FactionStateEnum.RETREAT, StarSystemData::getControllingFactionState)
            .returns(List.of(CONTROLLING_FACTION_ID), StarSystemData::getMinorFactions)
            .returns(List.of(Power.NAKATO_KAINE), StarSystemData::getPowers)
            .returns(List.of(minorFactionConflict), StarSystemData::getConflicts)
            .returns(POWERPLAY_STATE_CONTROL_PROGRESS, StarSystemData::getPowerplayStateControlProgress)
            .returns(POWERPLAY_STATE_REINFORCEMENT, StarSystemData::getPowerplayStateReinforcement)
            .returns(POWERPLAY_STATE_UNDERMINING, StarSystemData::getPowerplayStateUndermining)
            .returns(List.of(powerplayConflict), StarSystemData::getPowerplayConflicts);
    }

    @Test
    void controllingFactionIsNotInFactionList() {
        given(minorFaction.getFactionName()).willReturn("asd");
        given(minorFaction.getId()).willReturn(CONTROLLING_FACTION_ID);
        given(controllingFaction.getFactionName()).willReturn(FACTION_NAME);
        given(controllingFaction.getState()).willReturn("Retreat");
        given(minorFactionSaver.save(LAST_UPDATE, FACTION_NAME, FactionStateEnum.RETREAT)).willReturn(newMinorFaction);
        given(newMinorFaction.getId()).willReturn(CONTROLLING_FACTION_ID);

        assertThat(underTest.create(
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
            List.of(minorFactionConflict),
            POWERPLAY_STATE_CONTROL_PROGRESS,
            POWERPLAY_STATE_REINFORCEMENT,
            POWERPLAY_STATE_UNDERMINING,
            List.of(powerplayConflict)
        ))
            .returns(STAR_SYSTEM_ID, StarSystemData::getStarSystemId)
            .returns(LAST_UPDATE, StarSystemData::getLastUpdate)
            .returns(POPULATION, StarSystemData::getPopulation)
            .returns(Allegiance.ALLIANCE, StarSystemData::getAllegiance)
            .returns(EconomyEnum.AGRICULTURE, StarSystemData::getEconomy)
            .returns(EconomyEnum.COLONY, StarSystemData::getSecondaryEconomy)
            .returns(SecurityLevel.ANARCHY, StarSystemData::getSecurityLevel)
            .returns(Power.AISLING_DUVAL, StarSystemData::getControllingPower)
            .returns(PowerplayState.FORTIFIED, StarSystemData::getPowerplayState)
            .returns(CONTROLLING_FACTION_ID, StarSystemData::getControllingFactionId)
            .returns(FactionStateEnum.RETREAT, StarSystemData::getControllingFactionState)
            .returns(List.of(CONTROLLING_FACTION_ID), StarSystemData::getMinorFactions)
            .returns(List.of(Power.NAKATO_KAINE), StarSystemData::getPowers)
            .returns(List.of(minorFactionConflict), StarSystemData::getConflicts)
            .returns(POWERPLAY_STATE_CONTROL_PROGRESS, StarSystemData::getPowerplayStateControlProgress)
            .returns(POWERPLAY_STATE_REINFORCEMENT, StarSystemData::getPowerplayStateReinforcement)
            .returns(POWERPLAY_STATE_UNDERMINING, StarSystemData::getPowerplayStateUndermining)
            .returns(List.of(powerplayConflict), StarSystemData::getPowerplayConflicts);
    }

    @Test
    void nulls() {
        assertThat(underTest.create(
            STAR_SYSTEM_ID,
            LAST_UPDATE,
            POPULATION,
            Allegiance.ALLIANCE,
            EconomyEnum.AGRICULTURE,
            EconomyEnum.COLONY,
            SecurityLevel.ANARCHY,
            Power.AISLING_DUVAL,
            PowerplayState.FORTIFIED,
            null,
            null,
            null,
            null,
            POWERPLAY_STATE_CONTROL_PROGRESS,
            POWERPLAY_STATE_REINFORCEMENT,
            POWERPLAY_STATE_UNDERMINING,
            List.of(powerplayConflict)
        ))
            .returns(STAR_SYSTEM_ID, StarSystemData::getStarSystemId)
            .returns(LAST_UPDATE, StarSystemData::getLastUpdate)
            .returns(POPULATION, StarSystemData::getPopulation)
            .returns(Allegiance.ALLIANCE, StarSystemData::getAllegiance)
            .returns(EconomyEnum.AGRICULTURE, StarSystemData::getEconomy)
            .returns(EconomyEnum.COLONY, StarSystemData::getSecondaryEconomy)
            .returns(SecurityLevel.ANARCHY, StarSystemData::getSecurityLevel)
            .returns(Power.AISLING_DUVAL, StarSystemData::getControllingPower)
            .returns(PowerplayState.FORTIFIED, StarSystemData::getPowerplayState)
            .returns(null, StarSystemData::getControllingFactionId)
            .returns(null, StarSystemData::getControllingFactionState)
            .returns(null, StarSystemData::getMinorFactions)
            .returns(null, StarSystemData::getPowers)
            .returns(null, StarSystemData::getConflicts)
            .returns(POWERPLAY_STATE_CONTROL_PROGRESS, StarSystemData::getPowerplayStateControlProgress)
            .returns(POWERPLAY_STATE_REINFORCEMENT, StarSystemData::getPowerplayStateReinforcement)
            .returns(POWERPLAY_STATE_UNDERMINING, StarSystemData::getPowerplayStateUndermining)
            .returns(List.of(powerplayConflict), StarSystemData::getPowerplayConflicts);
    }
}