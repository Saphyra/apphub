package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventLabelMappingFactory {
    public EventLabelMapping create(UUID userId, UUID eventId, UUID labelId) {
        return EventLabelMapping.builder()
            .userId(userId)
            .eventId(eventId)
            .labelId(labelId)
            .build();
    }
}
