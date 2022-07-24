package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.query.OccurrenceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class CalendarQueryService {
    private final DaysOfMonthProvider daysOfMonthProvider;
    private final OccurrenceQueryService occurrenceQueryService;
    private final CalendarResponseFactory calendarResponseFactory;

    public List<CalendarResponse> getCalendar(UUID userId, ReferenceDate referenceDate) {
        Set<LocalDate> dates = daysOfMonthProvider.getDaysOfMonth(referenceDate.getMonth());
        dates.add(referenceDate.getDay());


        return fetchCalendar(userId, dates);
    }

    public List<CalendarResponse> getCalendarForMonth(UUID userId, LocalDate date) {
        Set<LocalDate> dates = daysOfMonthProvider.getDaysOfMonth(date);

        return fetchCalendar(userId, dates);
    }

    private List<CalendarResponse> fetchCalendar(UUID userId, Set<LocalDate> dates) {
        return occurrenceQueryService.getOccurrences(userId, dates)
            .entrySet()
            .stream()
            .map(entry -> calendarResponseFactory.create(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
