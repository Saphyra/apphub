package com.github.saphyra.apphub.service.diary.service.occurrence;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Component
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

        List<Event> events = eventDao.getByUserId(userId);

        return dates.stream()
            .sorted()
            .collect(Collectors.toMap(Function.identity(), date -> getOccurrences(date, events)));
    }

    private List<Occurrence> getOccurrences(LocalDate date, List<Event> events) {
        return events.stream()
            .flatMap(event -> fetchOccurrenceOfEvent(date, event))
            .collect(Collectors.toList());
    }

    private Stream<Occurrence> fetchOccurrenceOfEvent(LocalDate date, Event event) {
        log.info("Fetching occurrence of event {} on date {}", event.getEventId(), date);
        log.info("Event data: {}", event); //TODO debug

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        //No recurring occurrences before the start date
        if (currentDate.isBefore(event.getStartDate())) {
            log.info("Current date is before the event's start date"); //TODO debug
            return Stream.empty();
        }

        List<Occurrence> occurrences = occurrenceDao.getByEventId(event.getEventId());
        log.info("Occurrences related to event: {}", occurrences);//TODO debug

        Optional<Occurrence> currentDatesOccurrence = occurrences.stream()
            .filter(occurrence -> occurrence.getDate().equals(date))
            .findAny();
        log.info("Current date's occurrence: {}", currentDatesOccurrence);//TODO debug

        //If there is an occurrence already today, return that
        if (currentDatesOccurrence.isPresent()) {
            return Stream.of(currentDatesOccurrence.get());
        }

        switch (event.getRepetitionType()) {
            case ONE_TIME:
                //Handled by current date's occurrence
                break;
            case DAYS_OF_WEEK:
                List<DayOfWeek> repetitionTypeDaysOfWeeks = parseDaysOfWeek(event);
                log.info("RepetitionData days of week: {}", repetitionTypeDaysOfWeeks);//TODO debug

                if (repetitionTypeDaysOfWeeks.contains(currentDate.getDayOfWeek())) {
                    Occurrence occurrence = occurrences.stream()
                        .filter(existingOccurrence -> existingOccurrence.getDate().equals(date))
                        .findAny()
                        .orElseGet(() -> createOccurrence(currentDate, date, event));

                    log.info("Returning occurrence {}", occurrence);//TODO debug

                    return Stream.of(occurrence);
                }

                log.info("{} is not on a selected weekday.", date);//TODO debug

                return Stream.empty();
            case EVERY_X_DAYS:
                LocalDate timeOfLastEvent = occurrences.stream()
                    .filter(occurrence -> occurrence.getStatus() != OccurrenceStatus.VIRTUAL)
                    .map(Occurrence::getDate)
                    .max(Comparator.naturalOrder())
                    .orElse(event.getStartDate());
                log.info("Time of last event: {}", timeOfLastEvent);//TODO debug

                long dayDifference = ChronoUnit.DAYS.between(timeOfLastEvent, currentDate);
                log.info("dayDifference: {}", dayDifference);//TODO debug

                if (Integer.parseInt(event.getRepetitionData()) % dayDifference == 0) {
                    Occurrence occurrence = createOccurrence(currentDate, date, event);
                    log.info("Returning occurrence {}", occurrence);//TODO debug

                    return Stream.of(occurrence);
                }

                return Stream.empty();
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled repetitionType: " + event.getRepetitionType());
        }

        return Stream.empty();
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
