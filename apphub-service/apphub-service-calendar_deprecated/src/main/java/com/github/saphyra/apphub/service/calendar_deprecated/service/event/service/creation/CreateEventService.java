package com.github.saphyra.apphub.service.calendar_deprecated.service.event.service.creation;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.CreateEventRequest;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.CalendarQueryService;
import com.github.saphyra.apphub.service.calendar_deprecated.service.occurrence.service.OccurrenceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateEventService {
    private final CreateEventRequestValidator createEventRequestValidator;
    private final EventFactory eventFactory;
    private final EventDao eventDao;
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;
    private final CalendarQueryService calendarQueryService;

    @Transactional
    public List<CalendarResponse> createEvent(UUID userId, CreateEventRequest request) {
        createEventRequestValidator.validate(request);

        Event event = eventFactory.create(userId, request);
        eventDao.save(event);

        Occurrence occurrence = occurrenceFactory.createPending(event);
        occurrenceDao.save(occurrence);

        return calendarQueryService.getCalendar(userId, request.getReferenceDate());
    }
}
