package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class MinorFactionConverter extends ConverterBase<MinorFactionEntity, MinorFaction> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected MinorFactionEntity processDomainConversion(MinorFaction domain) {
        return MinorFactionEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .factionName(domain.getFactionName())
            .state(domain.getState())
            .build();
    }

    @Override
    protected MinorFaction processEntityConversion(MinorFactionEntity entity) {
        return MinorFaction.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .factionName(entity.getFactionName())
            .state(entity.getState())
            .build();
    }
}
