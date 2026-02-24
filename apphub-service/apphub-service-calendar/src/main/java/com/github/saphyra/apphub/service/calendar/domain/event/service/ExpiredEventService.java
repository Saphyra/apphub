package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ExpiredEventService {
    private final EventDao eventDao;
    private final EventMapper eventMapper;
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final EventRequestValidator eventRequestValidator;
    private final UpdateEventContextFactory updateEventContextFactory;

    public List<EventResponse> getExpiredEvents(UUID userId) {
        return eventDao.getByUserId(userId)
            .stream()
            .filter(this::isExpired)
            .map(eventMapper::toResponse)
            .toList();
    }

    private boolean isExpired(Event event) {
        if (event.isExpirationNotified()) {
            return false;
        }

        if (event.getRepetitionType() == RepetitionType.ONE_TIME) {
            return false;
        }

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        List<Occurrence> occurrences = occurrenceDao.getByEventId(event.getEventId());

        if (occurrences.isEmpty()) {
            return false;
        }

        LocalDate lastOccurrenceDate = occurrences.stream()
            .map(Occurrence::getDate)
            .max(LocalDate::compareTo)
            .orElseThrow();

        //Event is expired is the last occurrence us today or in the past.
        return !currentDate.isBefore(lastOccurrenceDate);
    }

    public void snooze(UUID eventId) {
        Event event = eventDao.findByIdValidated(eventId);

        event.setExpirationNotified(true);

        eventDao.save(event);
    }

    public void extend(UUID eventId, LocalDate extendUntil) {
        LocalDate startDate = dateTimeUtil.getCurrentDate();

        eventRequestValidator.validateDates(startDate, extendUntil);

        Event event = eventDao.findByIdValidated(eventId);
        if(event.getRepetitionType() == RepetitionType.ONE_TIME){
            throw ExceptionFactory.invalidParam("eventId", "must not be one-time event");
        }

        UpdateEventContext context = updateEventContextFactory.create(event);

        event.setStartDate(startDate);
        event.setStartDate(extendUntil);

        context.occurrenceRecreationNeeded();

        context.processChanges();
    }
}
