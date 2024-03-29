package com.github.saphyra.apphub.service.calendar.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceType;
import com.github.saphyra.apphub.service.calendar.service.occurrence.service.OccurrenceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
class DaysOfWeekEventHandler {
    private final DaysOfWeekParser daysOfWeekParser;
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;
    private final RepeatedEventPostCollector repeatedEventPostCollector;

    List<Occurrence> handleDaysOfWeekEvent(Event event, List<LocalDate> dates, OptionalMap<LocalDate, Occurrence> occurrenceMapping) {
        List<DayOfWeek> repetitionTypeDaysOfWeeks = daysOfWeekParser.parseDaysOfWeek(event);
        log.debug("RepetitionData days of week: {}", repetitionTypeDaysOfWeeks);

        List<Occurrence> occurrences = dates.stream()
            .filter(date -> !date.isBefore(event.getStartDate()))
            .filter(date -> repetitionTypeDaysOfWeeks.contains(date.getDayOfWeek()) || occurrenceMapping.getOptional(date).isPresent())
            .map(date -> occurrenceMapping.getOptional(date).orElseGet(() -> {
                Occurrence occurrence = occurrenceFactory.createVirtual(date, event, OccurrenceType.DEFAULT);
                occurrenceDao.save(occurrence);
                return occurrence;
            }))
            .collect(Collectors.toList());
        return repeatedEventPostCollector.collect(event, occurrences, dates);
    }
}
