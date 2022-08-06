package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.OccurrenceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class DaysOfMonthEventHandler {
    private final DaysOfMonthParser daysOfMonthParser;
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;

    public List<Occurrence> handleDaysOfMonthEvent(Event event, List<LocalDate> dates, OptionalMap<LocalDate, Occurrence> occurrenceMapping) {
        List<Integer> repetitionTypeDaysOfMonth = daysOfMonthParser.parseDaysOfMonth(event);

        return dates.stream()
            .filter(date -> !date.isBefore(event.getStartDate()))
            .filter(date -> repetitionTypeDaysOfMonth.contains(date.getDayOfMonth()) || occurrenceMapping.getOptional(date).isPresent())
            .map(date -> occurrenceMapping.getOptional(date).orElseGet(() -> {
                Occurrence occurrence = occurrenceFactory.createVirtual(date, event);
                occurrenceDao.save(occurrence);
                return occurrence;
            }))
            .collect(Collectors.toList());
    }
}
