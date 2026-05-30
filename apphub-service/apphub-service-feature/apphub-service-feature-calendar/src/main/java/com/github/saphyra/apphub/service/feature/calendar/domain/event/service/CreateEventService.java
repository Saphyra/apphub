package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.CreateOccurrenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateEventService {
    private final EventRequestValidator eventRequestValidator;
    private final EventFactory eventFactory;
    private final CreateOccurrenceService createOccurrenceService;
    private final CommonCalendarDao commonCalendarDao;

    @Transactional
    public UUID create(UUID userId, EventRequest request) {
        eventRequestValidator.validate(userId, request);

        Event event = eventFactory.create(userId, request);

        List<Occurrence> occurrences = createOccurrenceService.createOccurrences(userId, event.getEventId(), request);

        commonCalendarDao.saveNewEvent(event, occurrences, request.getLabels());

        return event.getEventId();
    }
}
