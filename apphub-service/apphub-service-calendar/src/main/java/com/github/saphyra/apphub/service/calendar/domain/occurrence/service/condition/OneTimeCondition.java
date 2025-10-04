package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

class OneTimeCondition implements RepetitionTypeCondition {
    @Override
    public List<LocalDate> getOccurrences(LocalDate startDate, LocalDate endDate, Integer repeatForDays, @Nullable LocalDate currentDate) {
        return Stream.iterate(startDate, date -> date.plusDays(1))
            .limit(repeatForDays)
            .toList();
    }

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays) {
        return ChronoUnit.DAYS.between(startDate, date) < repeatForDays;
    }
}
