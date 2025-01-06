package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.minor_faction;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StarSystemMinorFactionMappingConverter extends ConverterBase<StarSystemMinorFactionMappingEntity, StarSystemMinorFactionMapping> {
    private final UuidConverter uuidConverter;

    @Override
    protected StarSystemMinorFactionMappingEntity processDomainConversion(StarSystemMinorFactionMapping domain) {
        return StarSystemMinorFactionMappingEntity.builder()
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .minorFactionId(uuidConverter.convertDomain(domain.getMinorFactionId()))
            .build();
    }

    @Override
    protected StarSystemMinorFactionMapping processEntityConversion(StarSystemMinorFactionMappingEntity entity) {
        return StarSystemMinorFactionMapping.builder()
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .minorFactionId(uuidConverter.convertEntity(entity.getMinorFactionId()))
            .build();
    }
}
