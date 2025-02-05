package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ConflictingMinorFactionConverter extends ConverterBase<ConflictingMinorFactionEntity, ConflictingMinorFaction> {
    private final UuidConverter uuidConverter;

    @Override
    protected ConflictingMinorFactionEntity processDomainConversion(ConflictingMinorFaction domain) {
        return ConflictingMinorFactionEntity.builder()
            .conflictId(uuidConverter.convertDomain(domain.getConflictId()))
            .factionId(uuidConverter.convertDomain(domain.getFactionId()))
            .wonDays(domain.getWonDays())
            .stake(domain.getStake())
            .build();
    }

    @Override
    protected ConflictingMinorFaction processEntityConversion(ConflictingMinorFactionEntity entity) {
        return ConflictingMinorFaction.builder()
            .conflictId(uuidConverter.convertEntity(entity.getConflictId()))
            .factionId(uuidConverter.convertEntity(entity.getFactionId()))
            .wonDays(entity.getWonDays())
            .stake(entity.getStake())
            .build();
    }
}
