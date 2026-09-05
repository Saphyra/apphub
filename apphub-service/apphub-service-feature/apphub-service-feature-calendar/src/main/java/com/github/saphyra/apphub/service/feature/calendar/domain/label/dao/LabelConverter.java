package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LabelConverter extends ConverterBase<LabelEntity, Label> {
    private final UuidConverter uuidConverter;

    @Override
    protected LabelEntity processDomainConversion(Label domain) {
        String labelId = uuidConverter.convertDomain(domain.getLabelId());
        return LabelEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .labelId(labelId)
            .label(domain.getLabel())
            .build();
    }

    @Override
    protected Label processEntityConversion(LabelEntity entity) {
        return Label.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .label(entity.getLabel())
            .build();
    }
}
