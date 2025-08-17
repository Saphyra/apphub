package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

@RequiredArgsConstructor
public class DaysOfWeekCondition implements RepetitionTypeCondition {
    private final Collection<DayOfWeek> daysOfWeek;

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate date, Integer repeatForDays) {
        if (daysOfWeek.contains(date.getDayOfWeek())) {
            return true;
        }

        for (int i = 1; i <= repeatForDays; i++) {
            LocalDate previousDate = date.minusDays(i);
            if (daysOfWeek.contains(previousDate.getDayOfWeek())) {
                return true;
            }
        }

        return false;
    }
}
