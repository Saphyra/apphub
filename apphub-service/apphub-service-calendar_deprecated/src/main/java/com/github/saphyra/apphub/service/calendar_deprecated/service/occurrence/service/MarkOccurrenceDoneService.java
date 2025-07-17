package com.github.saphyra.apphub.service.calendar_deprecated.service.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.calendar_deprecated.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
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
