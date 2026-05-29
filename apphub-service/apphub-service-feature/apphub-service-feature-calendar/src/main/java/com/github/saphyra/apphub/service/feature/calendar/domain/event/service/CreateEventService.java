package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.CreateOccurrenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateEventService {
    private final EventRequestValidator eventRequestValidator;
    private final DeprecatedEventFactory eventFactory;
    private final EventLabelMappingService eventLabelMappingService;
    private final CreateOccurrenceService createOccurrenceService;
    private final DeprecatedEventDao eventDao;

    @Transactional
    public UUID create(UUID userId, EventRequest request) {
        eventRequestValidator.validate(request);

        DeprecatedEvent event = eventFactory.create(userId, request);

        eventLabelMappingService.addLabels(userId, event.getEventId(), request.getLabels());
        createOccurrenceService.createOccurrences(userId, event.getEventId(), request);

        eventDao.save(event);

        return event.getEventId();
    }
}
