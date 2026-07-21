package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
//TODO unit test
class LabelEventMappingConverter extends ConverterBase<LabelEventMappingEntity, LabelEventMapping> {
    private final UuidConverter uuidConverter;

    @Override
    protected LabelEventMappingEntity processDomainConversion(LabelEventMapping domain) {
        return LabelEventMappingEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .labelId(uuidConverter.convertDomain(domain.getLabelId()))
            .eventIds(domain.getEventIds().entrySet().stream().collect(Collectors.toMap(entry -> uuidConverter.convertDomain(entry.getKey()), entry -> uuidConverter.convertDomain(entry.getValue()))))
            .build();
    }

    @Override
    protected LabelEventMapping processEntityConversion(LabelEventMappingEntity entity) {
        return LabelEventMapping.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .eventIds(entity.getEventIds().entrySet().stream().collect(Collectors.toMap(entry -> uuidConverter.convertEntity(entry.getKey()), entry -> uuidConverter.convertEntity(entry.getValue()))))
            .build();
    }
}
