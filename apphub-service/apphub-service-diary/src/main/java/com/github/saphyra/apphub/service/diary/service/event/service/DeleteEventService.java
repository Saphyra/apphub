package com.github.saphyra.apphub.service.diary.service.event.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteEventService {
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final CalendarQueryService calendarQueryService;

    @Transactional
    public List<CalendarResponse> delete(UUID userId, UUID eventId, LocalDate date) {
        ValidationUtil.notNull(date, "date");

        Event event = eventDao.findByIdValidated(eventId);

        occurrenceDao.deleteByEventId(eventId);
        eventDao.delete(event);

        return calendarQueryService.getCalendar(userId, date);
    }
}
