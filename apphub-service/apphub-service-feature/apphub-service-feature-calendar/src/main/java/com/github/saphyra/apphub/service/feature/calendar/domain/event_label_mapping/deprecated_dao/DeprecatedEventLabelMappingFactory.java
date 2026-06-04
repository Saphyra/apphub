package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class DeprecatedEventLabelMappingFactory {
    public DeprecatedEventLabelMapping create(UUID userId, UUID eventId, UUID labelId) {
        return DeprecatedEventLabelMapping.builder()
            .userId(userId)
            .eventId(eventId)
            .labelId(labelId)
            .build();
    }
}
