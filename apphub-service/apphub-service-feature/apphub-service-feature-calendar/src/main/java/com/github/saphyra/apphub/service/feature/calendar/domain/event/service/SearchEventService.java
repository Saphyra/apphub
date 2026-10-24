package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query.OccurrenceObjectQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchEventService {
    private final EventResponseMapper eventResponseMapper;
    private final EventObjectQueryService  eventObjectQueryService;
    private final OccurrenceObjectQueryService occurrenceObjectQueryService;

    public List<EventResponse> search(UUID userId, String search) {
        ValidationUtil.minLength(search, 3, "searchText");

        List<Event> events = eventObjectQueryService.getEvents(userId)
            .stream()
            .filter(event -> eventMatches(event, search) || occurrenceMatches(userId, event.getEventId(), search))
            .toList();

        List<UUID> eventIds = events.stream()
            .map(Event::getEventId)
            .toList();

        List<Event> occurrenceEvents = occurrenceObjectQueryService.getOccurrences(userId)
            .entrySet()
            .stream()
            .filter(entry -> !eventIds.contains(entry.getKey().getEventId()))
            .filter(entry -> entry.getValue().stream().anyMatch(occurrence -> occurrenceMatches(occurrence, search)))
            .map(Map.Entry::getKey)
            .toList();

        return eventResponseMapper.toResponse(userId, Stream.concat(events.stream(), occurrenceEvents.stream()).toList());
    }

    private boolean occurrenceMatches(UUID userId, UUID eventId, String search) {
        return occurrenceObjectQueryService.getOccurrences(userId, eventId)
            .getEntity2()
            .stream()
            .anyMatch(occurrence -> occurrenceMatches(occurrence, search));
    }

    private boolean occurrenceMatches(Occurrence occurrence, String search) {
        return !occurrence.isMasked() && matches(search, occurrence.getNote());
    }

    private boolean eventMatches(Event event, String search) {
        return !event.isMasked() && (matches(search, event.getTitle()) || matches(search, event.getContent()));
    }

    private static boolean matches(String search, String value) {
        return value.toLowerCase().contains(search.toLowerCase());
    }
}
