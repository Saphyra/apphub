package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceResponseMapper {
    private final EventDao eventDao;

    public List<OccurrenceResponse> toResponse(UUID userId, List<Occurrence> occurrences) {
        List<UUID> eventIds = occurrences.stream()
            .map(Occurrence::getEventId)
            .distinct()
            .toList();

        Map<UUID, Event> events = eventDao.getByIds(userId, eventIds)
            .stream()
            .collect(Collectors.toMap(Event::getEventId, event -> event));

        return toResponse(events, occurrences);
    }

    public List<OccurrenceResponse> toResponse(Map<UUID, Event> events, List<Occurrence> occurrences) {
        return occurrences.stream()
            .map(occurrence -> toResponse(events.get(occurrence.getEventId()), occurrence))
            .toList();
    }

    public OccurrenceResponse toResponse(UUID userId, Occurrence occurrence) {
        Event event = eventDao.findByIdValidated(userId, occurrence.getEventId());

        return toResponse(event, occurrence);
    }

    private OccurrenceResponse toResponse(Event event, Occurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getFromEventIfNull(event, occurrence.getTime(), Event::getTime))
            .status(occurrence.getStatus())
            .title(event.getTitle())
            .content(event.getContent())
            .note(occurrence.getNote())
            .remindMeBeforeDays(getFromEventIfNull(event, occurrence.getRemindMeBeforeDays(), Event::getRemindMeBeforeDays))
            .reminded(occurrence.isReminded())
            .eventArchived(event.isArchived())
            .build();
    }

    private <T> T getFromEventIfNull(Event event, T value, Function<Event, T> mapper) {
        return Optional.ofNullable(value)
            .orElseGet(() -> mapper.apply(event));
    }
}
