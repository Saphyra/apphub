package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.diary.service.ReferenceDateValidator;
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
public class MarkOccurrenceDoneService {
    private final OccurrenceDao occurrenceDao;
    private final CalendarQueryService calendarQueryService;
    private final ReferenceDateValidator referenceDateValidator;

    public List<CalendarResponse> markDone(UUID userId, UUID occurrenceId, ReferenceDate referenceDate) {
        referenceDateValidator.validate(referenceDate);

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);

        occurrence.setStatus(OccurrenceStatus.DONE);
        occurrenceDao.save(occurrence);

        return calendarQueryService.getCalendar(userId, referenceDate);
    }
}
