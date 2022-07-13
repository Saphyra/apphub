package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MonthlyOccurrenceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class CalendarQueryService {
    private final DaysOfMonthProvider daysOfMonthProvider;
    private final MonthlyOccurrenceProvider monthlyOccurrenceProvider;
    private final CalendarResponseFactory calendarResponseFactory;

    public CalendarResponse getCalendarForDay(UUID userId, LocalDate date) {
        List<Occurrence> occurrences = monthlyOccurrenceProvider.getOccurrencesOfDay(userId, date);
        return calendarResponseFactory.create(date, occurrences);
    }

    public List<CalendarResponse> getCalendar(UUID userId, LocalDate date) {
        List<LocalDate> dates = daysOfMonthProvider.getDaysOfMonth(date);

        return monthlyOccurrenceProvider.getOccurrencesOfMonth(userId, dates)
            .entrySet()
            .stream()
            .map(entry -> calendarResponseFactory.create(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
