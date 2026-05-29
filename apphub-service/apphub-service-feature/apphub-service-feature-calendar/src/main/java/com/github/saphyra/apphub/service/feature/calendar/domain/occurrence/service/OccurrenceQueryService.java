package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.common.EventCache;
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
public class OccurrenceQueryService {
    private final DeprecatedOccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final DeprecatedEventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceMapper occurrenceMapper;
    private final DeprecatedEventDao eventDao;

    public List<OccurrenceResponse> getOccurrences(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
        LocalDate currentDate = dateTimeUtil.getCurrentDate();
        LazyLoadedField<List<UUID>> eventsOfLabel = new LazyLoadedField<>(() -> getEventsOfLabel(userId, labelId));
        EventCache eventCache = new EventCache(eventDao);
        Map<UUID, List<DeprecatedOccurrence>> occurrencesForEvents = occurrenceDao.getByUserId(userId)
            .stream()
            .filter(occurrence -> isNull(labelId) || eventsOfLabel.get().contains(occurrence.getEventId()))
            .collect(Collectors.groupingBy(DeprecatedOccurrence::getEventId));

        return occurrencesForEvents.entrySet()
            .stream()
            .flatMap(entry -> getOccurrencesForEvent(eventCache, entry.getKey(), entry.getValue(), currentDate, startDate, endDate).stream())
            .map(occurrence -> occurrenceMapper.toResponse(eventCache, occurrence))
            .toList();
    }

    private List<DeprecatedOccurrence> getOccurrencesForEvent(EventCache eventCache, UUID eventId, List<DeprecatedOccurrence> occurrences, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        List<DeprecatedOccurrence> result = new ArrayList<>();

        occurrences.stream()
            .flatMap(occurrence -> getOccurrencesToAdd(eventCache.get(eventId), occurrence, currentDate, startDate, endDate).stream())
            .forEach(result::add);

        return result;
    }

    private List<DeprecatedOccurrence> getOccurrencesToAdd(DeprecatedEvent event, DeprecatedOccurrence occurrence, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        List<DeprecatedOccurrence> result = new ArrayList<>();

        //Add occurrence if it is in boundaries
        if (isBetween(occurrence.getDate(), startDate, endDate)) {
            result.add(occurrence);
        }

        //Add an expired clone of occurrence if current date is in boundaries
        if (occurrence.getStatus() == OccurrenceStatus.EXPIRED && isBetween(currentDate, startDate, endDate)) {
            DeprecatedOccurrence clone = occurrence.toBuilder()
                .date(currentDate)
                .build();
            result.add(clone);
        }

        log.debug("Checking if {} should have a reminder", occurrence);
        //Add reminder if it is enabled, not yet reminded and in boundaries
        if (needReminder(event, occurrence)) {
            Integer minusDays = Optional.ofNullable(occurrence.getRemindMeBeforeDays())
                .orElse(event.getRemindMeBeforeDays());
            LocalDate reminderDate = occurrence.getDate()
                .minusDays(minusDays);
            //Reminder should not happen before current date
            if (reminderDate.isBefore(currentDate)) {
                log.debug("Reminder is before current date, setting to current date");
                reminderDate = currentDate;
            }

            if (isBetween(reminderDate, startDate, endDate)) {
                log.debug("Adding reminder for {} at {}", occurrence, reminderDate);
                DeprecatedOccurrence clone = occurrence.toBuilder()
                    .date(reminderDate)
                    .status(OccurrenceStatus.REMINDER)
                    .build();
                result.add(clone);
            }
        }

        return result;
    }

    private boolean needReminder(DeprecatedEvent event, DeprecatedOccurrence occurrence) {
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
            .map(DeprecatedEventLabelMapping::getEventId)
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
        DeprecatedOccurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        return occurrenceMapper.toResponse(occurrence);
    }
}
