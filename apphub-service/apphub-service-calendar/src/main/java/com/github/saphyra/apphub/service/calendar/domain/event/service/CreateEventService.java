package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.CreateOccurrenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CreateEventService {
    private final EventRequestValidator eventRequestValidator;
    private final EventFactory eventFactory;
    private final EventLabelMappingService eventLabelMappingService;
    private final CreateOccurrenceService createOccurrenceService;
    private final EventDao eventDao;

    @Transactional
    public void create(UUID userId, EventRequest request) {
        eventRequestValidator.validate(request);

        Event event = eventFactory.create(userId, request);

        eventLabelMappingService.addLabels(userId, event.getEventId(), request.getLabels());
        createOccurrenceService.createOccurrences(userId, event.getEventId(), request);

        eventDao.save(event);
    }
}
