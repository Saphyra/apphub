package com.github.saphyra.apphub.service.elite_base.dao.star_system_data;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.MinorFactionConflictDao;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.MinorFactionConflictSyncService;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.minor_faction.StarSystemMinorFactionMapping;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.minor_faction.StarSystemMinorFactionMappingDao;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.minor_faction.StarSystemMinorFactionMappingSyncService;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.power.StarSystemPowerMappingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StarSystemDataConverter extends ConverterBase<StarSystemDataEntity, StarSystemData> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final MinorFactionConflictDao minorFactionConflictDao;
    private final MinorFactionConflictSyncService minorFactionConflictSyncService;
    private final StarSystemMinorFactionMappingDao starSystemMinorFactionMappingDao;
    private final StarSystemMinorFactionMappingSyncService starSystemMinorFactionMappingSyncService;
    private final StarSystemPowerMappingDao starSystemPowerMappingDao;
    private final StarSystemPowerMappingSyncService starSystemPowerMappingSyncService;

    @Override
    protected StarSystemDataEntity processDomainConversion(StarSystemData domain) {
        minorFactionConflictSyncService.sync(domain.getStarSystemId(), domain.getConflicts());
        starSystemMinorFactionMappingSyncService.sync(domain.getStarSystemId(), domain.getMinorFactions());
        starSystemPowerMappingSyncService.sync(domain.getStarSystemId(), domain.getPowers());

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
            .minorFactions(new LazyLoadedField<>(() -> starSystemMinorFactionMappingDao.getByStarSystemId(starSystemId).stream().map(StarSystemMinorFactionMapping::getMinorFactionId).toList()))
            .powers(new LazyLoadedField<>(() -> starSystemPowerMappingDao.getByStarSystemId(starSystemId).stream().map(StarSystemPowerMapping::getPower).toList()))
            .conflicts(new LazyLoadedField<>(() -> minorFactionConflictDao.getByStarSystemId(starSystemId)))
            .build();
    }
}
