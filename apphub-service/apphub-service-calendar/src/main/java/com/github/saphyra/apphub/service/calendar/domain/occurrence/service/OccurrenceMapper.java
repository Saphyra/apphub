package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
class OccurrenceMapper {
    private final EventDao eventDao;

    OccurrenceResponse toResponse(EventCache eventCache, Occurrence occurrence) {
        return toResponse(eventCache::get, occurrence);
    }

    OccurrenceResponse toResponse(Occurrence occurrence) {
        LazyLoadedField<Event> event = new LazyLoadedField<>(() -> eventDao.findByIdValidated(occurrence.getEventId()));

        return toResponse(eventId -> event.get(), occurrence);
    }

    private OccurrenceResponse toResponse(Function<UUID, Event> eventProvider, Occurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getFromEventIfNull(eventProvider, occurrence.getEventId(), occurrence.getTime(), Event::getTime))
            .status(occurrence.getStatus())
            .title(eventProvider.apply(occurrence.getEventId()).getTitle())
            .content(eventProvider.apply(occurrence.getEventId()).getContent())
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
