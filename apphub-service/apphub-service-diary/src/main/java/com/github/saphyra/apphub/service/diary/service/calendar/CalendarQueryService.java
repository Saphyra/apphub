package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.service.diary.service.occurrence.MonthlyOccurrenceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
class CalendarQueryService {
    private final MonthlyOccurrenceProvider monthlyOccurrenceProvider;
    private final CalendarResponseFactory calendarResponseFactory;

    public List<CalendarResponse> getCalendar(UUID userId, LocalDate date) {
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

        return monthlyOccurrenceProvider.getOccurrencesOfMonth(userId, dates)
            .entrySet()
            .stream()
            .map(entry -> calendarResponseFactory.create(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
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
