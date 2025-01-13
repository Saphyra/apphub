package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BodyRingConverter extends ConverterBase<BodyRingEntity, BodyRing> {
    private final UuidConverter uuidConverter;

    @Override
    protected BodyRingEntity processDomainConversion(BodyRing domain) {
        return BodyRingEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .bodyId(uuidConverter.convertDomain(domain.getBodyId()))
            .name(domain.getName())
            .type(domain.getType())
            .innerRadius(domain.getInnerRadius())
            .outerRadius(domain.getOuterRadius())
            .mass(domain.getMass())
            .build();
    }

    @Override
    protected BodyRing processEntityConversion(BodyRingEntity entity) {
        return BodyRing.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .bodyId(uuidConverter.convertEntity(entity.getBodyId()))
            .name(entity.getName())
            .type(entity.getType())
            .innerRadius(entity.getInnerRadius())
            .outerRadius(entity.getOuterRadius())
            .mass(entity.getMass())
            .build();
    }
}
