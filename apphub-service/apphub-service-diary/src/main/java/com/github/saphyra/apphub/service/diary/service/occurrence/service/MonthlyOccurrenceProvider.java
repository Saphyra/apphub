package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO refactor - split
//TODO unit test
public class MonthlyOccurrenceProvider {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final OccurrenceFactory occurrenceFactory;
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Transactional
    public List<Occurrence> getOccurrencesOfDay(UUID userId, LocalDate date) {
        return getOccurrencesOfMonth(userId, List.of(date))
            .get(date);
    }

    @Transactional
    public Map<LocalDate, List<Occurrence>> getOccurrencesOfMonth(UUID userId, List<LocalDate> dates) {
        log.info("Deleting virtual dates of user {}", userId);
        occurrenceDao.deleteVirtualByUserId(userId);

        List<LocalDate> sortedDates = dates.stream()
            .sorted()
            .collect(Collectors.toList());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        Map<LocalDate, List<Occurrence>> occurrences = eventDao.getByUserId(userId)
            .stream()
            .flatMap(event -> fetchOccurrenceOfEvent(event, currentDate, sortedDates).stream())
            .peek(occurrence -> {
                if (occurrence.getDate().isBefore(currentDate) && occurrence.getStatus() != OccurrenceStatus.EXPIRED) {
                    log.info("{} is expired.", occurrence); //TODO debug
                    occurrence.setStatus(OccurrenceStatus.EXPIRED);
                    occurrenceDao.save(occurrence);
                }
            })
            .collect(Collectors.groupingBy(Occurrence::getDate));
        return sortedDates.stream()
            .collect(Collectors.toMap(Function.identity(), date -> occurrences.getOrDefault(date, Collections.emptyList())));
    }

    private List<Occurrence> fetchOccurrenceOfEvent(Event event, LocalDate currentDate, List<LocalDate> dates) {
        log.info("Fetching Occurrences for event {}", event); //TODO debug
        List<Occurrence> occurrences = occurrenceDao.getByEventId(event.getEventId());
        log.info("Occurrences related to event: {}", occurrences);//TODO debug

        OptionalMap<LocalDate, Occurrence> occurrenceMapping = CollectionUtils.mapToOptionalMap(occurrences, Occurrence::getDate, Function.identity());

        switch (event.getRepetitionType()) {
            case ONE_TIME:
                return handleOneTimeEvent(occurrences, dates);
            case DAYS_OF_WEEK:
                return handleDaysOfWeekEvent(event, currentDate, dates, occurrenceMapping);
            case EVERY_X_DAYS:
                return handleEveryXDayEvent(event, currentDate, dates, occurrences, occurrenceMapping);
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled repetitionType: " + event.getRepetitionType());
        }
    }

    private List<Occurrence> handleEveryXDayEvent(Event event, LocalDate currentDate, List<LocalDate> dates, List<Occurrence> occurrences, OptionalMap<LocalDate, Occurrence> occurrenceMapping) {
        return dates.stream()
            .filter(date -> !date.isBefore(event.getStartDate()))
            .filter(date -> {
                if (occurrenceMapping.containsKey(date)) { //Return existing events
                    return true;
                }

                LocalDate timeOfLastEvent = occurrences.stream()
                    .filter(occurrence -> occurrence.getStatus() != OccurrenceStatus.VIRTUAL)
                    .filter(occurrence -> occurrence.getDate().isBefore(date))
                    .map(Occurrence::getDate)
                    .max(Comparator.naturalOrder())
                    .orElse(event.getStartDate());
                log.info("Time of last event: {}", timeOfLastEvent);//TODO debug

                long dayDifference = ChronoUnit.DAYS.between(timeOfLastEvent, date);
                log.info("dayDifference between {} and {}: {}", timeOfLastEvent, date, dayDifference);//TODO debug

                return dayDifference % Integer.parseInt(event.getRepetitionData()) == 0;
            })
            .map(date -> occurrenceMapping.getOptional(date).orElseGet(() -> createOccurrence(currentDate, date, event)))
            .collect(Collectors.toList());
    }

    private List<Occurrence> handleDaysOfWeekEvent(Event event, LocalDate currentDate, List<LocalDate> dates, OptionalMap<LocalDate, Occurrence> occurrenceMapping) {
        List<DayOfWeek> repetitionTypeDaysOfWeeks = parseDaysOfWeek(event);
        log.info("RepetitionData days of week: {}", repetitionTypeDaysOfWeeks);//TODO debug

        return dates.stream()
            .filter(date -> !date.isBefore(event.getStartDate()))
            .filter(date -> repetitionTypeDaysOfWeeks.contains(date.getDayOfWeek()))
            .map(date -> occurrenceMapping.getOptional(date).orElseGet(() -> createOccurrence(currentDate, date, event)))
            .collect(Collectors.toList());
    }

    private List<Occurrence> handleOneTimeEvent(List<Occurrence> occurrences, List<LocalDate> dates) {
        Occurrence occurrence = occurrences.get(0);
        if (dates.contains(occurrence.getDate())) {
            return List.of(occurrence);
        } else {
            return Collections.emptyList();
        }
    }

    private List<DayOfWeek> parseDaysOfWeek(Event event) {
        return objectMapperWrapper.readArrayValue(event.getRepetitionData(), DayOfWeek[].class);
    }

    private Occurrence createOccurrence(LocalDate currentDate, LocalDate date, Event event) {
        Occurrence occurrence = date.isBefore(currentDate) ? occurrenceFactory.createExpired(date, event) : occurrenceFactory.createVirtual(date, event);
        log.info("Occurrence created: {}", occurrence); //TODO debug
        occurrenceDao.save(occurrence);
        return occurrence;
    }
}
