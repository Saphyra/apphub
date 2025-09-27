package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public interface RepetitionTypeCondition {
    /**
     * Returns the dates of the occurrences
     *
     * @param currentDate null if past dates should be returned
     */
    default List<LocalDate> getOccurrences(LocalDate startDate, LocalDate endDate, Integer repeatForDays, @Nullable LocalDate currentDate) {
        return Stream.iterate(startDate, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(startDate, endDate) + repeatForDays)
            .filter(date -> shouldHaveOccurrence(startDate, endDate, date, repeatForDays))
            .filter(occurrenceDate -> isNull(currentDate) || !occurrenceDate.isBefore(currentDate))
            .toList();
    }

    boolean shouldHaveOccurrence(LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays);
}
