package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BodyMaterialConverter extends ConverterBase<BodyMaterialEntity, BodyMaterial> {
    private final UuidConverter uuidConverter;

    @Override
    protected BodyMaterialEntity processDomainConversion(BodyMaterial domain) {
        return BodyMaterialEntity.builder()
            .id(BodyMaterialEntityId.builder()
                .bodyId(uuidConverter.convertDomain(domain.getBodyId()))
                .material(domain.getMaterial())
                .build())
            .percent(domain.getPercent())
            .build();
    }

    @Override
    protected BodyMaterial processEntityConversion(BodyMaterialEntity entity) {
        return BodyMaterial.builder()
            .bodyId(uuidConverter.convertEntity(entity.getId().getBodyId()))
            .material(entity.getId().getMaterial())
            .percent(entity.getPercent())
            .build();
    }
}