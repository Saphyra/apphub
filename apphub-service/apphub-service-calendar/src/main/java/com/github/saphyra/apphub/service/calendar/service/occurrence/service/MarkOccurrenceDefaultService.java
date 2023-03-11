package com.github.saphyra.apphub.service.calendar.service.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.calendar.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkOccurrenceDefaultService {
    private final OccurrenceDao occurrenceDao;
    private final CalendarQueryService calendarQueryService;
    private final ReferenceDateValidator referenceDateValidator;

    public List<CalendarResponse> markDefault(UUID userId, UUID occurrenceId, ReferenceDate referenceDate) {
        referenceDateValidator.validate(referenceDate);

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);

        occurrence.setStatus(OccurrenceStatus.PENDING);
        occurrenceDao.save(occurrence);

        return calendarQueryService.getCalendar(userId, referenceDate);
    }
}
