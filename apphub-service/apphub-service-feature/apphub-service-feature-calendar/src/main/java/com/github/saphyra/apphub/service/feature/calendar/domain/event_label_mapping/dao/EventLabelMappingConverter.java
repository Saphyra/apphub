package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class EventLabelMappingConverter extends ConverterBase<EventLabelMappingEntity, EventLabelMapping> {
    private final UuidConverter uuidConverter;

    @Override
    protected EventLabelMappingEntity processDomainConversion(EventLabelMapping domain) {
        return EventLabelMappingEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .labelIds(domain.getLabelIds().entrySet().stream().collect(Collectors.toMap(entry -> uuidConverter.convertDomain(entry.getKey()), entry -> uuidConverter.convertDomain(entry.getValue()))))
            .build();
    }

    @Override
    protected EventLabelMapping processEntityConversion(EventLabelMappingEntity entity) {
        return EventLabelMapping.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .labelIds(entity.getLabelIds().entrySet().stream().collect(Collectors.toMap(entry -> uuidConverter.convertEntity(entry.getKey()), entry -> uuidConverter.convertEntity(entry.getValue()))))
            .build();
    }
}
