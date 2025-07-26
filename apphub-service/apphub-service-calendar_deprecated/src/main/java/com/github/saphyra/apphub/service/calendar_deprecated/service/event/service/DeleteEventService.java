package com.github.saphyra.apphub.service.calendar_deprecated.service.event.service;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar_deprecated.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteEventService {
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final CalendarQueryService calendarQueryService;
    private final ReferenceDateValidator referenceDateValidator;

    @Transactional
    public List<CalendarResponse> delete(UUID userId, UUID eventId, ReferenceDate referenceDate) {
        referenceDateValidator.validate(referenceDate);

        occurrenceDao.deleteByEventId(eventId);
        eventDao.deleteById(eventId);

        return calendarQueryService.getCalendar(userId, referenceDate);
    }
}
