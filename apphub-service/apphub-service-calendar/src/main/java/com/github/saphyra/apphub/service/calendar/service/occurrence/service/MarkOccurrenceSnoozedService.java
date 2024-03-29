package com.github.saphyra.apphub.service.calendar.service.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.calendar.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkOccurrenceSnoozedService {
    private final OccurrenceDao occurrenceDao;
    private final CalendarQueryService calendarQueryService;
    private final ReferenceDateValidator referenceDateValidator;

    public List<CalendarResponse> markSnoozed(UUID userId, UUID occurrenceId, ReferenceDate referenceDate) {
        referenceDateValidator.validate(referenceDate);

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);

        if (occurrence.getStatus() == OccurrenceStatus.DONE) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.INVALID_STATUS, occurrenceId + " is already done.");
        }

        occurrence.setStatus(OccurrenceStatus.SNOOZED);
        occurrenceDao.save(occurrence);

        return calendarQueryService.getCalendar(userId, referenceDate);
    }
}
