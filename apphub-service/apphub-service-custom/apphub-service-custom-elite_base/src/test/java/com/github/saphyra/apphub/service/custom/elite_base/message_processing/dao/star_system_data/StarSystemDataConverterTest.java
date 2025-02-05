package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflictDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflictSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.minor_faction.StarSystemMinorFactionMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.minor_faction.StarSystemMinorFactionMappingSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.power.StarSystemPowerMappingSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.SecurityLevel;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StarSystemDataConverterTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long POPULATION = 34L;
    private static final UUID CONTROLLING_FACTION_ID = UUID.randomUUID();
    private static final UUID MINOR_FACTION_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String LAST_UPDATE_STRING = "last-update";
    private static final String CONTROLLING_FACTION_ID_STRING = "controlling-faction-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @Mock
    private MinorFactionConflictDao minorFactionConflictDao;

    @Mock
    private MinorFactionConflictSyncService minorFactionConflictSyncService;

    @Mock
    private StarSystemMinorFactionMappingDao starSystemMinorFactionMappingDao;

    @Mock
    private StarSystemMinorFactionMappingSyncService starSystemMinorFactionMappingSyncService;

    @Mock
    private StarSystemPowerMappingDao starSystemPowerMappingDao;

    @Mock
    private StarSystemPowerMappingSyncService starSystemPowerMappingSyncService;

    @InjectMocks
    private StarSystemDataConverter underTest;

    @Mock
    private MinorFactionConflict minorFactionConflict;

    @Mock
    private StarSystemMinorFactionMapping starSystemMinorFactionMapping;

    @Mock
    private StarSystemPowerMapping starSystemPowerMapping;

    @Test
    void convertDomain() {
        StarSystemData domain = StarSystemData.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .lastUpdate(LAST_UPDATE)
            .population(POPULATION)
            .allegiance(Allegiance.ALLIANCE)
            .economy(EconomyEnum.AGRICULTURE)
            .secondaryEconomy(EconomyEnum.COLONY)
            .securityLevel(SecurityLevel.ANARCHY)
            .controllingPower(Power.ARISSA_LAVIGNY_DUVAL)
            .powerplayState(PowerplayState.FORTIFIED)
            .controllingFactionId(CONTROLLING_FACTION_ID)
            .controllingFactionState(FactionStateEnum.BLIGHT)
            .conflicts(LazyLoadedField.loaded(List.of(minorFactionConflict)))
            .minorFactions(LazyLoadedField.loaded(List.of(MINOR_FACTION_ID)))
            .powers(LazyLoadedField.loaded(List.of(Power.NAKATO_KAINE)))
            .build();

        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);
        given(uuidConverter.convertDomain(CONTROLLING_FACTION_ID)).willReturn(CONTROLLING_FACTION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(STAR_SYSTEM_ID_STRING, StarSystemDataEntity::getStarSystemId)
            .returns(LAST_UPDATE_STRING, StarSystemDataEntity::getLastUpdate)
            .returns(POPULATION, StarSystemDataEntity::getPopulation)
            .returns(Allegiance.ALLIANCE, StarSystemDataEntity::getAllegiance)
            .returns(EconomyEnum.AGRICULTURE, StarSystemDataEntity::getEconomy)
            .returns(EconomyEnum.COLONY, StarSystemDataEntity::getSecondaryEconomy)
            .returns(SecurityLevel.ANARCHY, StarSystemDataEntity::getSecurityLevel)
            .returns(Power.ARISSA_LAVIGNY_DUVAL, StarSystemDataEntity::getControllingPower)
            .returns(PowerplayState.FORTIFIED, StarSystemDataEntity::getPowerplayState)
            .returns(CONTROLLING_FACTION_ID_STRING, StarSystemDataEntity::getControllingFactionId)
            .returns(FactionStateEnum.BLIGHT, StarSystemDataEntity::getControllingFactionState);

        then(minorFactionConflictSyncService).should().sync(STAR_SYSTEM_ID, List.of(minorFactionConflict));
        then(starSystemMinorFactionMappingSyncService).should().sync(STAR_SYSTEM_ID, List.of(MINOR_FACTION_ID));
        then(starSystemPowerMappingSyncService).should().sync(STAR_SYSTEM_ID, List.of(Power.NAKATO_KAINE));
    }

    @Test
    void convertEntity() {
        StarSystemDataEntity entity = StarSystemDataEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .population(POPULATION)
            .allegiance(Allegiance.ALLIANCE)
            .economy(EconomyEnum.AGRICULTURE)
            .secondaryEconomy(EconomyEnum.COLONY)
            .securityLevel(SecurityLevel.ANARCHY)
            .controllingPower(Power.ARISSA_LAVIGNY_DUVAL)
            .powerplayState(PowerplayState.FORTIFIED)
            .controllingFactionId(CONTROLLING_FACTION_ID_STRING)
            .controllingFactionState(FactionStateEnum.BLIGHT)
            .build();

        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);
        given(uuidConverter.convertEntity(CONTROLLING_FACTION_ID_STRING)).willReturn(CONTROLLING_FACTION_ID);

        given(minorFactionConflictDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(minorFactionConflict));
        given(starSystemMinorFactionMappingDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(starSystemMinorFactionMapping));
        given(starSystemPowerMappingDao.getByStarSystemId(STAR_SYSTEM_ID)).willReturn(List.of(starSystemPowerMapping));
        given(starSystemMinorFactionMapping.getMinorFactionId()).willReturn(MINOR_FACTION_ID);
        given(starSystemPowerMapping.getPower()).willReturn(Power.NAKATO_KAINE);

        assertThat(underTest.convertEntity(entity))
            .returns(STAR_SYSTEM_ID, StarSystemData::getStarSystemId)
            .returns(LAST_UPDATE, StarSystemData::getLastUpdate)
            .returns(POPULATION, StarSystemData::getPopulation)
            .returns(Allegiance.ALLIANCE, StarSystemData::getAllegiance)
            .returns(EconomyEnum.AGRICULTURE, StarSystemData::getEconomy)
            .returns(EconomyEnum.COLONY, StarSystemData::getSecondaryEconomy)
            .returns(SecurityLevel.ANARCHY, StarSystemData::getSecurityLevel)
            .returns(Power.ARISSA_LAVIGNY_DUVAL, StarSystemData::getControllingPower)
            .returns(PowerplayState.FORTIFIED, StarSystemData::getPowerplayState)
            .returns(CONTROLLING_FACTION_ID, StarSystemData::getControllingFactionId)
            .returns(FactionStateEnum.BLIGHT, StarSystemData::getControllingFactionState)
            .returns(List.of(minorFactionConflict), StarSystemData::getConflicts)
            .returns(List.of(MINOR_FACTION_ID), StarSystemData::getMinorFactions)
            .returns(List.of(Power.NAKATO_KAINE), StarSystemData::getPowers);

    }
}