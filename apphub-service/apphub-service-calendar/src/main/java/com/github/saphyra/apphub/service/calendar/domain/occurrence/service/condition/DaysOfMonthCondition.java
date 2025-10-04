package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@RequiredArgsConstructor
class DaysOfMonthCondition implements RepetitionTypeCondition {
    private final Collection<Integer> daysOfMonth;

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate endDate,  LocalDate date, Integer repeatForDays) {
        if (daysOfMonth.contains(date.getDayOfMonth()) && !date.isAfter(endDate)) {
            return true;
        }

        for (int i = 1; i < repeatForDays; i++) {
            LocalDate previousDate = date.minusDays(i);
            if (!previousDate.isBefore(startDate) && daysOfMonth.contains(previousDate.getDayOfMonth())) {
                return true;
            }
        }

        return false;
    }
}
