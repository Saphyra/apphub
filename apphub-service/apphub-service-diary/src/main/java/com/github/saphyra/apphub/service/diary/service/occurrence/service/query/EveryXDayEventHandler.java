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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
class EveryXDayEventHandler {
    private final OccurrenceFactory occurrenceFactory;
    private final DateOfLastOccurrenceProvider dateOfLastOccurrenceProvider;
    private final OccurrenceDao occurrenceDao;

    List<Occurrence> handleEveryXDayEvent(Event event, List<LocalDate> dates, OptionalMap<LocalDate, Occurrence> occurrenceMapping) {
        return dates.stream()
            .filter(date -> !date.isBefore(event.getStartDate()))
            .filter(date -> {
                if (occurrenceMapping.containsKey(date)) { //Return existing events
                    return true;
                }

                LocalDate timeOfLastEvent = dateOfLastOccurrenceProvider.getDateOfLastOccurrence(occurrenceMapping.values(), event, date);
                log.debug("Time of last event: {}", timeOfLastEvent);

                long dayDifference = ChronoUnit.DAYS.between(timeOfLastEvent, date);
                log.debug("dayDifference between {} and {}: {}", timeOfLastEvent, date, dayDifference);

                return dayDifference % Integer.parseInt(event.getRepetitionData()) == 0;
            })
            .map(date -> occurrenceMapping.getOptional(date).orElseGet(() -> {
                Occurrence occurrence = occurrenceFactory.createVirtual(date, event);
                occurrenceDao.save(occurrence);
                return occurrence;
            }))
            .collect(Collectors.toList());
    }
}
