package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventQueryService {
    private final EventResponseMapper eventResponseMapper;
    private final EventObjectQueryService eventObjectQueryService;

    public List<EventResponse> getEvents(UUID userId, UUID labelId) {
        List<Event> events;
        if (isNull(labelId)) {
            events = eventObjectQueryService.getEvents(userId);
        } else {
            events = eventObjectQueryService.getEventsOfLabel(userId, labelId);
        }

        return eventResponseMapper.toResponse(userId, events);
    }

    public List<EventResponse> getLabellessEvents(UUID userId) {
        return eventResponseMapper.toResponse(userId, eventObjectQueryService.getLabellessEvents(userId).toList());
    }

    public EventResponse getEvent(UUID userId, UUID eventId) {
        return eventObjectQueryService.findEvent(userId, eventId)
            .map(event -> eventResponseMapper.toResponse(userId, event))
            .orElseThrow(() -> ExceptionFactory.notFound(userId + " has no access to event " + eventId + " or event does not exist."));
    }
}
