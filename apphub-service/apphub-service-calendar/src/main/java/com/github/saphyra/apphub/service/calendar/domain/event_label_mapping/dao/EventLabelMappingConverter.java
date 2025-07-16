package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventLabelMappingConverter extends ConverterBase<EventLabelMappingEntity, EventLabelMapping> {
    protected final UuidConverter uuidConverter;

    @Override
    protected EventLabelMappingEntity processDomainConversion(EventLabelMapping domain) {
        return EventLabelMappingEntity.builder()
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .labelId(uuidConverter.convertDomain(domain.getLabelId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .build();
    }

    @Override
    protected EventLabelMapping processEntityConversion(EventLabelMappingEntity entity) {
        return EventLabelMapping.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .build();
    }
}
