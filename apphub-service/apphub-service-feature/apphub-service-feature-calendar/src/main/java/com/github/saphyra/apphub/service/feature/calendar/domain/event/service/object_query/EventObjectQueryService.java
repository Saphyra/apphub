package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventObjectQueryService {
    private final FindEventForOperationService findEventForOperationService;
    private final EventsOfLabelQueryService eventsOfLabelQueryService;
    private final GetEventsService getEventsService;
    private final FindEventService findEventService;
    private final GetLabellessEventsService getLabellessEventsService;

    public Optional<Event> findEvent(UUID userId, UUID eventId, Operation operation) {
        return findEventForOperationService.findEvent(userId, eventId, operation);
    }

    public List<Event> getEventsOfLabel(UUID userId, UUID labelId) {
        return eventsOfLabelQueryService.getEventsOfLabel(userId, labelId);
    }

    public List<Event> getEvents(UUID userId) {
        return getEventsService.getEvents(userId);
    }

    public Optional<Event> findEvent(UUID userId, UUID eventId) {
        return findEventService.findEvent(userId, eventId);
    }

    public Stream<Event> getLabellessEvents(UUID userId) {
        return getLabellessEventsService.getLabellessEvents(userId);
    }
}
