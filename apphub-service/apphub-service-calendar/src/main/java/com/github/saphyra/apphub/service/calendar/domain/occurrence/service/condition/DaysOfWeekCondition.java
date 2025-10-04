package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

@RequiredArgsConstructor
public class DaysOfWeekCondition implements RepetitionTypeCondition {
    private final Collection<DayOfWeek> daysOfWeek;

    @Override
    public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays) {
        if (daysOfWeek.contains(date.getDayOfWeek()) && !date.isAfter(endDate)) {
            return true;
        }

        for (int i = 1; i < repeatForDays; i++) {
            LocalDate previousDate = date.minusDays(i);
            if (!previousDate.isBefore(startDate) && daysOfWeek.contains(previousDate.getDayOfWeek())) {
                return true;
            }
        }

        return false;
    }
}
