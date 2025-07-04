package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class StarSystemPowerMappingConverter extends ConverterBase<StarSystemPowerMappingEntity, StarSystemPowerMapping> {
    private final UuidConverter uuidConverter;

    @Override
    protected StarSystemPowerMappingEntity processDomainConversion(StarSystemPowerMapping domain) {
        return StarSystemPowerMappingEntity.builder()
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .power(domain.getPower())
            .build();
    }

    @Override
    protected StarSystemPowerMapping processEntityConversion(StarSystemPowerMappingEntity entity) {
        return StarSystemPowerMapping.builder()
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .power(entity.getPower())
            .build();
    }
}
