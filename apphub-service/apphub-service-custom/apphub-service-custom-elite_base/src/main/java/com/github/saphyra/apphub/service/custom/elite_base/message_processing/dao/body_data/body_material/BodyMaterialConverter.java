package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_material;

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
            .id(uuidConverter.convertDomain(domain.getId()))
            .bodyId(uuidConverter.convertDomain(domain.getBodyId()))
            .material(domain.getMaterial())
            .percent(domain.getPercent())
            .build();
    }

    @Override
    protected BodyMaterial processEntityConversion(BodyMaterialEntity entity) {
        return BodyMaterial.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .bodyId(uuidConverter.convertEntity(entity.getBodyId()))
            .material(entity.getMaterial())
            .percent(entity.getPercent())
            .build();
    }
}