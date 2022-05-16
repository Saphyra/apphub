package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
class CalendarToResponseConverter {
    private final CalendarEventProvider calendarEventProvider;

    CalendarResponse convert(UUID userId, LocalDate date) {
        return CalendarResponse.builder()
            .date(date)
            .events(calendarEventProvider.getEvents(userId, date))
            .build();
    }
}
