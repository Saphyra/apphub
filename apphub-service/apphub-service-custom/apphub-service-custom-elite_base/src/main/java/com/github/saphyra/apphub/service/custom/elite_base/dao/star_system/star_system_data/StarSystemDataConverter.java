package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflictDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflictSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction.StarSystemMinorFactionMappingSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflictDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflictSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StarSystemDataConverter extends ConverterBase<StarSystemDataEntity, StarSystemData> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final MinorFactionConflictDao minorFactionConflictDao;
    private final MinorFactionConflictSyncService minorFactionConflictSyncService;
    private final StarSystemMinorFactionMappingDao starSystemMinorFactionMappingDao;
    private final StarSystemMinorFactionMappingSyncService starSystemMinorFactionMappingSyncService;
    private final StarSystemPowerMappingDao starSystemPowerMappingDao;
    private final StarSystemPowerMappingSyncService starSystemPowerMappingSyncService;
    private final PowerplayConflictSyncService powerplayConflictSyncService;
    private final PowerplayConflictDao powerplayConflictDao;

    @Override
    protected StarSystemDataEntity processDomainConversion(StarSystemData domain) {
        minorFactionConflictSyncService.sync(domain.getStarSystemId(), domain.getConflicts());
        starSystemMinorFactionMappingSyncService.sync(domain.getStarSystemId(), domain.getMinorFactions());
        starSystemPowerMappingSyncService.sync(domain.getStarSystemId(), domain.getPowers());
        powerplayConflictSyncService.sync(domain.getStarSystemId(), domain.getPowerplayConflicts());

        return StarSystemDataEntity.builder()
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .population(domain.getPopulation())
            .allegiance(domain.getAllegiance())
            .economy(domain.getEconomy())
            .secondaryEconomy(domain.getSecondaryEconomy())
            .securityLevel(domain.getSecurityLevel())
            .controllingPower(domain.getControllingPower())
            .powerplayState(domain.getPowerplayState())
            .controllingFactionId(uuidConverter.convertDomain(domain.getControllingFactionId()))
            .controllingFactionState(domain.getControllingFactionState())
            .powerplayStateControlProgress(domain.getPowerplayStateControlProgress())
            .powerplayStateReinforcement(domain.getPowerplayStateReinforcement())
            .powerplayStateUndermining(domain.getPowerplayStateUndermining())
            .build();
    }

    @Override
    protected StarSystemData processEntityConversion(StarSystemDataEntity entity) {
        UUID starSystemId = uuidConverter.convertEntity(entity.getStarSystemId());
        return StarSystemData.builder()
            .starSystemId(starSystemId)
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .population(entity.getPopulation())
            .allegiance(entity.getAllegiance())
            .economy(entity.getEconomy())
            .secondaryEconomy(entity.getSecondaryEconomy())
            .securityLevel(entity.getSecurityLevel())
            .controllingPower(entity.getControllingPower())
            .powerplayState(entity.getPowerplayState())
            .controllingFactionId(uuidConverter.convertEntity(entity.getControllingFactionId()))
            .controllingFactionState(entity.getControllingFactionState())
            .powerplayStateControlProgress(entity.getPowerplayStateControlProgress())
            .powerplayStateReinforcement(entity.getPowerplayStateReinforcement())
            .powerplayStateUndermining(entity.getPowerplayStateUndermining())
            .minorFactions(new LazyLoadedField<>(() -> starSystemMinorFactionMappingDao.getByStarSystemId(starSystemId).stream().map(StarSystemMinorFactionMapping::getMinorFactionId).toList()))
            .powers(new LazyLoadedField<>(() -> starSystemPowerMappingDao.getByStarSystemId(starSystemId).stream().map(StarSystemPowerMapping::getPower).toList()))
            .conflicts(new LazyLoadedField<>(() -> minorFactionConflictDao.getByStarSystemId(starSystemId)))
            .powerplayConflicts(new LazyLoadedField<List<PowerplayConflict>>(() -> powerplayConflictDao.getByStarSystemId(starSystemId)))
            .build();
    }
}
