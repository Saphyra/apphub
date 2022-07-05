package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteOccurrenceService {
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final CalendarQueryService calendarQueryService;

    public List<CalendarResponse> delete(UUID userId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        Event event = eventDao.findByIdValidated(occurrence.getEventId());

        occurrenceDao.delete(occurrence);
        eventDao.delete(event);

        return calendarQueryService.getCalendar(userId, occurrence.getDate());
    }
}
