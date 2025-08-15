package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceMapper {
    private final DateTimeUtil dateTimeUtil;
    private final EventDao eventDao;

    OccurrenceResponse toResponse(EventCache eventCache, Occurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getFromEventIfNull(eventCache::get, occurrence.getEventId(), occurrence.getTime(), Event::getTime))
            .status(calculateOccurrenceStatus(occurrence.getStatus(), occurrence.getDate()))
            .note(occurrence.getNote())
            .remindMeBeforeDays(getFromEventIfNull(eventCache::get, occurrence.getEventId(), occurrence.getRemindMeBeforeDays(), Event::getRemindMeBeforeDays))
            .reminded(occurrence.getReminded())
            .build();
    }

    public OccurrenceResponse toResponse(Occurrence occurrence) {
        return toResponse(eventDao::findByIdValidated, occurrence);
    }

    private OccurrenceStatus calculateOccurrenceStatus(OccurrenceStatus status, LocalDate date) {
        if (status == OccurrenceStatus.PENDING && date.isBefore(dateTimeUtil.getCurrentDate())) {
            return OccurrenceStatus.EXPIRED;
        }

        return status;
    }

    private OccurrenceResponse toResponse(Function<UUID, Event> eventProvider, Occurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getFromEventIfNull(eventProvider, occurrence.getEventId(), occurrence.getTime(), Event::getTime))
            .status(calculateOccurrenceStatus(occurrence.getStatus(), occurrence.getDate()))
            .note(occurrence.getNote())
            .remindMeBeforeDays(getFromEventIfNull(eventProvider, occurrence.getEventId(), occurrence.getRemindMeBeforeDays(), Event::getRemindMeBeforeDays))
            .reminded(occurrence.getReminded())
            .build();
    }

    private <T> T getFromEventIfNull(Function<UUID, Event> eventProvider, UUID eventId, T value, Function<Event, T> mapper) {
        return Optional.ofNullable(value)
            .orElseGet(() -> mapper.apply(eventProvider.apply(eventId)));
    }
}
