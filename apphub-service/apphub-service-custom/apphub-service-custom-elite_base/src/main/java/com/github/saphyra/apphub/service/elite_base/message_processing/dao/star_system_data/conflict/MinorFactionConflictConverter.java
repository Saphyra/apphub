package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction.ConflictingMinorFactionSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class MinorFactionConflictConverter extends ConverterBase<MinorFactionConflictEntity, MinorFactionConflict> {
    private final UuidConverter uuidConverter;
    private final ConflictingMinorFactionDao conflictingMinorFactionDao;
    private final ConflictingMinorFactionSyncService conflictingMinorFactionSyncService;

    @Override
    protected MinorFactionConflictEntity processDomainConversion(MinorFactionConflict domain) {
        conflictingMinorFactionSyncService.sync(domain.getId(), domain.getConflictingMinorFactions());

        return MinorFactionConflictEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .status(domain.getStatus())
            .warType(domain.getWarType())
            .build();
    }

    @Override
    protected MinorFactionConflict processEntityConversion(MinorFactionConflictEntity entity) {
        UUID conflictId = uuidConverter.convertEntity(entity.getId());
        return MinorFactionConflict.builder()
            .id(conflictId)
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .warType(entity.getWarType())
            .status(entity.getStatus())
            .conflictingMinorFactions(conflictingMinorFactionDao.getByConflictId(conflictId))
            .build();
    }
}
