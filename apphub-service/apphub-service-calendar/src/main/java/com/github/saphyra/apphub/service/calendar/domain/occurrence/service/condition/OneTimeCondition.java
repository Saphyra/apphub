package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

class OneTimeCondition implements RepetitionTypeCondition {
    @Override
    public List<LocalDate> getOccurrences(LocalDate startDate, LocalDate endDate, Integer repeatForDays, @Nullable LocalDate currentDate) {
        return Stream.iterate(startDate, date -> date.plusDays(1))
            .limit(repeatForDays)
            .filter(occurrenceDate -> isNull(currentDate) || !occurrenceDate.isBefore(currentDate))
            .toList();
    }

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays) {
        return ChronoUnit.DAYS.between(startDate, date) < repeatForDays;
    }
}
