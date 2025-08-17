package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
class EveryXDaysCondition implements RepetitionTypeCondition {
    private final int days;

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate date, Integer repeatForDays) {
        return ChronoUnit.DAYS.between(startDate, date) % days < repeatForDays;
    }
}
