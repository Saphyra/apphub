package com.github.saphyra.apphub.service.diary.service.event.service.creation;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarResponseFactory;
import com.github.saphyra.apphub.service.diary.service.occurrence.MonthlyOccurrenceProvider;
import com.github.saphyra.apphub.service.diary.service.occurrence.OccurrenceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CreateEventService {
    private final CreateEventRequestValidator createEventRequestValidator;
    private final EventFactory eventFactory;
    private final EventDao eventDao;
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;
    private final MonthlyOccurrenceProvider monthlyOccurrenceProvider;
    private final CalendarResponseFactory calendarResponseFactory;

    @Transactional
    public CalendarResponse createEvent(UUID userId, CreateEventRequest request) {
        createEventRequestValidator.validate(request);

        Event event = eventFactory.create(userId, request);
        eventDao.save(event);

        Occurrence occurrence = occurrenceFactory.createPending(event);
        occurrenceDao.save(occurrence);

        List<Occurrence> occurrences = monthlyOccurrenceProvider.getOccurrencesOfDay(userId, request.getDate());
        return calendarResponseFactory.create(request.getDate(), occurrences);
    }
}
