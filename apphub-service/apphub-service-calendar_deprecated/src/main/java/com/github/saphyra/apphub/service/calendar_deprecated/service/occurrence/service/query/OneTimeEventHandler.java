package com.github.saphyra.apphub.service.calendar_deprecated.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
class OneTimeEventHandler {
    private final RepeatedEventPostCollector repeatedEventPostCollector;

    List<Occurrence> handleOneTimeEvent(Event event, List<Occurrence> occurrences, List<LocalDate> dates) {
        Occurrence occurrence = occurrences.stream()
            .filter(o -> o.getType() == OccurrenceType.DEFAULT)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Default occurrence not found for event " + event.getEventId()));
        if (dates.contains(occurrence.getDate())) {
            return repeatedEventPostCollector.collect(event, occurrences, dates);
        } else {
            return Collections.emptyList();
        }
    }
}
