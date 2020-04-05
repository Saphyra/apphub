package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EventProcessorConverter extends ConverterBase<EventProcessorEntity, EventProcessor> {
    private final UuidConverter uuidConverter;

    @Override
    protected EventProcessor processEntityConversion(EventProcessorEntity entity) {
        return EventProcessor.builder()
            .eventProcessorId(uuidConverter.convertEntity(entity.getEventProcessorId()))
            .serviceName(entity.getServiceName())
            .url(entity.getUrl())
            .eventName(entity.getEventName())
            .lastAccess(entity.getLastAccess())
            .build();
    }

    @Override
    protected EventProcessorEntity processDomainConversion(EventProcessor domain) {
        return EventProcessorEntity.builder()
            .eventProcessorId(uuidConverter.convertDomain(domain.getEventProcessorId()))
            .serviceName(domain.getServiceName())
            .url(domain.getUrl())
            .eventName(domain.getEventName())
            .lastAccess(domain.getLastAccess())
            .build();
    }
}
