package com.github.saphyra.apphub.service.diary.service.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class DaysOfMonthProvider {

    List<LocalDate> getDaysOfMonth(LocalDate date) {
        List<LocalDate> dates = new ArrayList<>();

        LocalDate lastAdded = date;
        while (shouldAddBefore(date, lastAdded.minusDays(1))) {
            lastAdded = lastAdded.minusDays(1);
            dates.add(lastAdded);
        }

        lastAdded = date;
        while (shouldAddAfter(date, lastAdded.plusDays(1))) {
            lastAdded = lastAdded.plusDays(1);
            dates.add(lastAdded);
        }

        dates.add(date);

        return dates;
    }

    private boolean shouldAddAfter(LocalDate date, LocalDate dateToAdd) {
        if (date.getMonth().equals(dateToAdd.getMonth())) {
            return true;
        }

        return dateToAdd.getDayOfWeek() != DayOfWeek.MONDAY;
    }

    private boolean shouldAddBefore(LocalDate date, LocalDate dateToAdd) {
        if (date.getMonth().equals(dateToAdd.getMonth())) {
            return true;
        }

        return dateToAdd.getDayOfWeek() != DayOfWeek.SUNDAY;
    }
}
