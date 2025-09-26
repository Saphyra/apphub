package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceCreator;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceRecreator;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeCondition;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
abstract class AbstractOccurrenceProcessor implements OccurrenceCreator, OccurrenceRecreator {
    private static final EnumSet<OccurrenceStatus> EXPIRED_OCCURRENCE_DELETE_STATUSES = EnumSet.of(OccurrenceStatus.PENDING, OccurrenceStatus.EXPIRED);
    private static final EnumSet<OccurrenceStatus> FUTURE_OCCURRENCE_DELETE_STATUSES = EnumSet.of(OccurrenceStatus.PENDING, OccurrenceStatus.EXPIRED);

    protected final OccurrenceFactory occurrenceFactory;
    protected final OccurrenceDao occurrenceDao;
    protected final RepetitionTypeConditionSelector repetitionTypeConditionSelector;
    protected final DateTimeUtil dateTimeUtil;

    @Override
    public void createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        List<LocalDate> dates = getConditions(request.getRepetitionData())
            .getOccurrences(request.getStartDate(), getEndDate(request), request.getRepeatForDays(), currentDate);
        if (dates.isEmpty()) {
            throw new IllegalStateException("Cannot create occurrences for event " + eventId + " because no occurrence dates were generated.");
        }

        dates.stream()
            .map(date -> occurrenceFactory.create(userId, eventId, date, null, null)) //Null parameters to use the event's values
            .forEach(occurrenceDao::save);
    }

    @Override
    public void recreateOccurrences(UpdateEventContext context) {
        Event event = context.getEvent();
        log.info("Recreating occurrences for event {}", event.getEventId());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        Map<LocalDate, List<Occurrence>> occurrencesByDate = context.getOccurrences()
            .stream()
            .collect(Collectors.groupingBy(Occurrence::getDate));

        log.info("Current occurrences: {}", occurrencesByDate); //TODO debug

        LocalDate endDate = getEndDate(event);
        List<LocalDate> datesOfOccurrences = getConditions(event.getRepetitionData())
            .getOccurrences(event.getStartDate(), endDate, event.getRepeatForDays(), currentDate)
            .stream()
            .toList();
        log.info("New occurrence dates: {}", datesOfOccurrences); //TODO debug
        if (datesOfOccurrences.isEmpty()) {
            throw new IllegalStateException("Cannot recreate occurrences for event " + event.getEventId() + " because no occurrence dates were generated.");
        }

        // Delete occurrences that are not in the new range
        context.deleteOccurrences(occurrence -> shouldDelete(currentDate, occurrence, datesOfOccurrences));

        datesOfOccurrences.forEach(date -> {
            // If there is no occurrence for the date, create one
            if (!occurrencesByDate.containsKey(date)) {
                Occurrence occurrence = occurrenceFactory.create(event.getUserId(), event.getEventId(), date, null, null); //Null parameters to use the event's values
                context.addOccurrence(occurrence);
            }
        });
    }

    /**
     * @return true, if occurrence should be deleted
     */
    private boolean shouldDelete(LocalDate currentDate, Occurrence occurrence, List<LocalDate> datesOfOccurrences) {
        //Keep occurrences that are still in the new range (avoid unnecessary recreation)
        if (datesOfOccurrences.contains(occurrence.getDate())) {
            log.info("{} should not be deleted, because it is still in the new range.", occurrence); //TODO debug
            return false;
        }

        //Keep history if possible
        if (occurrence.getDate().isBefore(currentDate)) {
            boolean result = EXPIRED_OCCURRENCE_DELETE_STATUSES.contains(occurrence.getStatus());
            if (result) {
                log.info("{} should be deleted, because it is expired and has status {}.", occurrence, occurrence.getStatus()); //TODO debug
            } else {
                log.info("{} should not be deleted, because it is expired, but has status {}.", occurrence, occurrence.getStatus()); //TODO debug
            }
            return result;
        }

        //Keep completed future occurrences
        boolean result = FUTURE_OCCURRENCE_DELETE_STATUSES.contains(occurrence.getStatus());
        if (result) {
            log.info("{} should be deleted, because it is future and has status {}.", occurrence, occurrence.getStatus()); //TODO debug
        } else {
            log.info("{} should not be deleted, because it is future, but has status {}.", occurrence, occurrence.getStatus()); //TODO debug
        }
        return result;
    }

    protected LocalDate getEndDate(EventRequest request) {
        return request.getEndDate();
    }

    protected LocalDate getEndDate(Event event) {
        return event.getEndDate();
    }

    private RepetitionTypeCondition getConditions(Object repetitionData) {
        return repetitionTypeConditionSelector.get(getRepetitionType(), repetitionData);
    }
}
