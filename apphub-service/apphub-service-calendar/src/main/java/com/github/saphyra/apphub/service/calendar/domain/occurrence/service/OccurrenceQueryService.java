package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceQueryService {
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceMapper occurrenceMapper;
    private final EventDao eventDao;

    public List<OccurrenceResponse> getOccurrences(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
        LocalDate currentDate = dateTimeUtil.getCurrentDate();
        LazyLoadedField<List<UUID>> eventsOfLabel = new LazyLoadedField<>(() -> getEventsOfLabel(userId, labelId));
        EventCache eventCache = new EventCache(eventDao);
        Map<UUID, List<Occurrence>> occurrencesForEvents = occurrenceDao.getByUserId(userId)
            .stream()
            .filter(occurrence -> isNull(labelId) || eventsOfLabel.get().contains(occurrence.getEventId()))
            .collect(Collectors.groupingBy(Occurrence::getEventId));

        return occurrencesForEvents.entrySet()
            .stream()
            .flatMap(entry -> getOccurrencesForEvent(eventCache, entry.getKey(), entry.getValue(), currentDate, startDate, endDate).stream())
            .map(occurrence -> occurrenceMapper.toResponse(eventCache, occurrence))
            .toList();
    }

    private List<Occurrence> getOccurrencesForEvent(EventCache eventCache, UUID eventId, List<Occurrence> occurrences, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        List<Occurrence> result = new ArrayList<>();

        occurrences.stream()
            .flatMap(occurrence -> getOccurrencesToAdd(eventCache.get(eventId), occurrence, currentDate, startDate, endDate).stream())
            .forEach(result::add);

        return result;
    }

    private List<Occurrence> getOccurrencesToAdd(Event event, Occurrence occurrence, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        List<Occurrence> result = new ArrayList<>();

        //Add occurrence if it is in boundaries
        if (isBetween(occurrence.getDate(), startDate, endDate)) {
            result.add(occurrence);
        }

        //Add an expired clone of occurrence if current date is in boundaries
        if (occurrence.getStatus() == OccurrenceStatus.EXPIRED && isBetween(currentDate, startDate, endDate)) {
            Occurrence clone = occurrence.toBuilder()
                .date(currentDate)
                .build();
            result.add(clone);
        }

        log.info("Checking if {} should have a reminder", occurrence); //TODO remove
        //Add reminder if it is enabled, not yet reminded and in boundaries
        if (needReminder(event, occurrence)) {
            Integer minusDays = Optional.ofNullable(occurrence.getRemindMeBeforeDays())
                .orElse(event.getRemindMeBeforeDays());
            LocalDate reminderDate = occurrence.getDate()
                .minusDays(minusDays);

            //Reminder should not happen before current date
            if (reminderDate.isBefore(currentDate)) {
                log.info("Reminder is before current date, setting to current date"); //TODO remove
                reminderDate = currentDate;
            }
            if (isBetween(reminderDate, startDate, endDate)) {
                log.info("Adding reminder for {} at {}", occurrence, reminderDate); //TODO remove
                Occurrence clone = occurrence.toBuilder()
                    .date(reminderDate)
                    .status(OccurrenceStatus.REMINDER)
                    .build();
                result.add(clone);
            }
        }

        return result;
    }

    private boolean needReminder(Event event, Occurrence occurrence) {
        boolean defaultReminder = nonNull(event.getRemindMeBeforeDays()) && event.getRemindMeBeforeDays() > 0;
        boolean occurrenceReminder = nonNull(occurrence.getRemindMeBeforeDays()) && occurrence.getRemindMeBeforeDays() > 0;

        return (defaultReminder || occurrenceReminder) && !occurrence.getReminded();
    }

    private static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private List<UUID> getEventsOfLabel(UUID userId, UUID labelId) {
        return eventLabelMappingDao.getByUserIdAndLabelId(userId, labelId)
            .stream()
            .map(EventLabelMapping::getEventId)
            .collect(Collectors.toList());
    }

    public List<OccurrenceResponse> getOccurrencesOfEvent(UUID eventId) {
        EventCache eventCache = new EventCache(eventDao);

        return occurrenceDao.getByEventId(eventId)
            .stream()
            .map(occurrence -> occurrenceMapper.toResponse(eventCache, occurrence))
            .toList();
    }

    public OccurrenceResponse getOccurrence(UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        return occurrenceMapper.toResponse(occurrence);
    }
}
