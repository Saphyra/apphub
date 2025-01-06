package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state.MinorFactionStateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state.MinorFactionStateSyncService;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state.StateStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MinorFactionConverter extends ConverterBase<MinorFactionEntity, MinorFaction> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final MinorFactionStateDao minorFactionStateDao;
    private final MinorFactionStateSyncService minorFactionStateSyncService;

    @Override
    protected MinorFactionEntity processDomainConversion(MinorFaction domain) {
        minorFactionStateSyncService.sync(domain.getId(), domain.getActiveStates(), domain.getPendingStates(), domain.getRecoveringStates());

        return MinorFactionEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .factionName(domain.getFactionName())
            .state(domain.getState())
            .influence(domain.getInfluence())
            .allegiance(domain.getAllegiance())
            .build();
    }

    @Override
    protected MinorFaction processEntityConversion(MinorFactionEntity entity) {
        UUID minorFactionId = uuidConverter.convertEntity(entity.getId());
        return MinorFaction.builder()
            .id(minorFactionId)
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .factionName(entity.getFactionName())
            .state(entity.getState())
            .influence(entity.getInfluence())
            .allegiance(entity.getAllegiance())
            .activeStates(new LazyLoadedField<>(() -> minorFactionStateDao.getByMinorFactionIdAndStatus(minorFactionId, StateStatus.ACTIVE)))
            .pendingStates(new LazyLoadedField<>(() -> minorFactionStateDao.getByMinorFactionIdAndStatus(minorFactionId, StateStatus.PENDING)))
            .recoveringStates(new LazyLoadedField<>(() -> minorFactionStateDao.getByMinorFactionIdAndStatus(minorFactionId, StateStatus.RECOVERING)))
            .build();
    }
}
