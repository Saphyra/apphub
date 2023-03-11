package com.github.saphyra.apphub.service.calendar.service.occurrence.service.query;

import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

@Component
@RequiredArgsConstructor
@Slf4j
class DateOfLastOccurrenceProvider {
    LocalDate getDateOfLastOccurrence(Collection<Occurrence> occurrences, Event event, LocalDate date) {
        return occurrences.stream()
            .filter(occurrence -> occurrence.getStatus() != OccurrenceStatus.VIRTUAL)
            .filter(occurrence -> occurrence.getDate().isBefore(date))
            .map(Occurrence::getDate)
            .max(Comparator.naturalOrder())
            .orElse(event.getStartDate());
    }
}
