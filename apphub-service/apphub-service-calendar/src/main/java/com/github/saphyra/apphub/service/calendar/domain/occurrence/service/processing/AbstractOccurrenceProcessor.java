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

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
abstract class AbstractOccurrenceProcessor implements OccurrenceCreator, OccurrenceRecreator {
    private static final EnumSet<OccurrenceStatus> EXPIRED_OCCURRENCE_DELETE_STATUSES = EnumSet.of(OccurrenceStatus.PENDING, OccurrenceStatus.EXPIRED);
    private static final EnumSet<OccurrenceStatus> FUTURE_OCCURRENCE_DELETE_STATUSES = EnumSet.of(OccurrenceStatus.PENDING, OccurrenceStatus.SNOOZED, OccurrenceStatus.EXPIRED);

    protected final OccurrenceFactory occurrenceFactory;
    protected final OccurrenceDao occurrenceDao;
    protected final RepetitionTypeConditionSelector repetitionTypeConditionSelector;
    protected final DateTimeUtil dateTimeUtil;

    @Override
    public void createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        List<LocalDate> dates = getConditions()
            .getOccurrences(request.getStartDate(), getEndDate(request), request.getRepeatForDays(), currentDate);
        if (dates.isEmpty()) {
            //TODO throw exception
        }

        dates.stream()
            .map(date -> occurrenceFactory.create(userId, eventId, date, null, null))
            .forEach(occurrenceDao::save);
    }

    @Override
    public void recreateOccurrences(UpdateEventContext context) {
        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        Map<LocalDate, List<Occurrence>> occurrencesByDate = context.getOccurrences()
            .stream()
            .collect(Collectors.groupingBy(Occurrence::getDate));

        Event event = context.getEvent();

        List<LocalDate> datesOfOccurrences = getConditions()
            .getOccurrences(event.getStartDate(), getEndDate(event), event.getRepeatForDays(), currentDate)
            .stream()
            .toList();
        if (datesOfOccurrences.isEmpty()) {
            //TODO throw exception
        }

        // Delete occurrences that are not in the new range
        context.deleteOccurrences(occurrence -> shouldDelete(currentDate, occurrence) && !datesOfOccurrences.contains(occurrence.getDate()));

        datesOfOccurrences.forEach(date -> {
            // If there is no occurrence for the date, create one
            if (!occurrencesByDate.containsKey(date)) {
                Occurrence occurrence = occurrenceFactory.create(event.getUserId(), event.getEventId(), date, null, null);
                context.addOccurrence(occurrence);
            }
        });
    }

    private boolean shouldDelete(LocalDate currentDate, Occurrence occurrence) {
        //Keep history if possible
        if (currentDate.isBefore(occurrence.getDate())) {
            return EXPIRED_OCCURRENCE_DELETE_STATUSES.contains(occurrence.getStatus());
        }

        //Keep completed future occurrences
        return FUTURE_OCCURRENCE_DELETE_STATUSES.contains(occurrence.getStatus());
    }

    protected LocalDate getEndDate(EventRequest request) {
        return request.getEndDate();
    }

    protected LocalDate getEndDate(Event event) {
        return event.getEndDate();
    }

    private RepetitionTypeCondition getConditions() {
        return repetitionTypeConditionSelector.get(getRepetitionType(), null);
    }
}
