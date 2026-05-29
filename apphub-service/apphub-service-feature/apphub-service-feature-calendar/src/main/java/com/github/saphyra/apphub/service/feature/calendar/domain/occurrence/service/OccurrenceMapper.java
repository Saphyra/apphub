package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.common.EventCache;
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
    private final DeprecatedEventDao eventDao;

    OccurrenceResponse toResponse(EventCache eventCache, DeprecatedOccurrence occurrence) {
        return toResponse(eventCache::get, occurrence);
    }

    OccurrenceResponse toResponse(DeprecatedOccurrence occurrence) {
        LazyLoadedField<DeprecatedEvent> event = new LazyLoadedField<>(() -> eventDao.findByIdValidated(occurrence.getEventId()));

        return toResponse(_ -> event.get(), occurrence);
    }

    private OccurrenceResponse toResponse(Function<UUID, DeprecatedEvent> eventProvider, DeprecatedOccurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getFromEventIfNull(eventProvider, occurrence.getEventId(), occurrence.getTime(), DeprecatedEvent::getTime))
            .status(occurrence.getStatus())
            .title(eventProvider.apply(occurrence.getEventId()).getTitle())
            .content(eventProvider.apply(occurrence.getEventId()).getContent())
            .note(Optional.ofNullable(occurrence.getNote()).orElse(""))
            .remindMeBeforeDays(getFromEventIfNull(eventProvider, occurrence.getEventId(), occurrence.getRemindMeBeforeDays(), DeprecatedEvent::getRemindMeBeforeDays))
            .reminded(occurrence.getReminded())
            .eventArchived(eventProvider.apply(occurrence.getEventId()).isArchived())
            .build();
    }

    private <T> T getFromEventIfNull(Function<UUID, DeprecatedEvent> eventProvider, UUID eventId, T value, Function<DeprecatedEvent, T> mapper) {
        return Optional.ofNullable(value)
            .orElseGet(() -> mapper.apply(eventProvider.apply(eventId)));
    }
}
