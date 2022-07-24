package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.OccurrenceToResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarResponseFactory {
    private final OccurrenceToResponseConverter occurrenceToResponseConverter;

    public CalendarResponse create(LocalDate date, List<Occurrence> occurrences) {
        return CalendarResponse.builder()
            .date(date)
            .events(occurrenceToResponseConverter.convert(occurrences))
            .build();
    }
}
