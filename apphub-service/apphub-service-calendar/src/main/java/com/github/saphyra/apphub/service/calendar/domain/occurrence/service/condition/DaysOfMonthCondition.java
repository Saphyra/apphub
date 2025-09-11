package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@RequiredArgsConstructor
class DaysOfMonthCondition implements RepetitionTypeCondition {
    private final Collection<Integer> daysOfMonth;

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate date, Integer repeatForDays) {
        if (daysOfMonth.contains(date.getDayOfMonth())) {
            return true;
        }

        for (int i = 1; i < repeatForDays; i++) {
            LocalDate previousDate = date.minusDays(i);
            if (daysOfMonth.contains(previousDate.getDayOfMonth())) {
                return true;
            }
        }

        return false;
    }
}
