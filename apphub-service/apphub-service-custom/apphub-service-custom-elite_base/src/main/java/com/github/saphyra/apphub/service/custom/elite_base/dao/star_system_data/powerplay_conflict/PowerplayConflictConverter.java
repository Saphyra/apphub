package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PowerplayConflictConverter extends ConverterBase<PowerplayConflictEntity, PowerplayConflict> {
    private final UuidConverter uuidConverter;

    @Override
    protected PowerplayConflictEntity processDomainConversion(PowerplayConflict domain) {
        return PowerplayConflictEntity.builder()
            .id(PowerplayConflictEntityId.builder()
                .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
                .power(domain.getPower())
                .build())
            .conflictProgress(domain.getConflictProgress())
            .build();
    }

    @Override
    protected PowerplayConflict processEntityConversion(PowerplayConflictEntity entity) {
        return PowerplayConflict.builder()
            .starSystemId(uuidConverter.convertEntity(entity.getId().getStarSystemId()))
            .power(entity.getId().getPower())
            .conflictProgress(entity.getConflictProgress())
            .build();
    }
}
