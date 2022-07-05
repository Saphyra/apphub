package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MarkOccurrenceDoneService {
    private final OccurrenceDao occurrenceDao;
    private final CalendarQueryService calendarQueryService;

    public CalendarResponse markDone(UUID userId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);

        occurrence.setStatus(OccurrenceStatus.DONE);
        occurrenceDao.save(occurrence);

        return calendarQueryService.getCalendarForDay(userId, occurrence.getDate());
    }
}
