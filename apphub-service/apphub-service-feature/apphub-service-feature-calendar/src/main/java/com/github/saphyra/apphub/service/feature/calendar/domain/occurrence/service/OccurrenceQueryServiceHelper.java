package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class OccurrenceQueryServiceHelper {
    private final OccurrenceDao occurrenceDao;

    List<Occurrence> getOccurrencesBetween(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<String> buckets = getBuckets(startDate, endDate);

        return occurrenceDao.getByBuckets(userId, buckets)
            .stream()
            .filter(occurrence -> !occurrence.getDate().isBefore(startDate) && !occurrence.getDate().isAfter(endDate))
            .toList();
    }

    private List<String> getBuckets(LocalDate startDate, LocalDate endDate) {
        List<String> buckets = new ArrayList<>();
        LocalDate date = startDate.withDayOfMonth(1);
        while (!date.isAfter(endDate)) {
            buckets.add(date.getYear() + "-" + date.getMonthValue());
            date = date.plusMonths(1);
        }
        return buckets;
    }

     List<Occurrence> getOccurrencesToAdd(Event event, Occurrence occurrence, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
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
        boolean defaultReminder = event.getRemindMeBeforeDays() > 0;
        boolean occurrenceReminder = nonNull(occurrence.getRemindMeBeforeDays()) && occurrence.getRemindMeBeforeDays() > 0;

        return (defaultReminder || occurrenceReminder) && !occurrence.isReminded();
    }

    private static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
