package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.ObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventQueryService {
    private final EventDao eventDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventResponseMapper eventResponseMapper;
    private final ObjectQueryService objectQueryService;

    public List<EventResponse> getEvents(UUID userId, UUID labelId) {
        List<Event> events;
        if (isNull(labelId)) {
            events = objectQueryService.getEvents(userId);
        } else {
            events = objectQueryService.getEventsOfLabel(userId, labelId);
        }

        return eventResponseMapper.toResponse(userId, events);
    }

    //TODO return shared labelless events
    public List<EventResponse> getLabellessEvents(UUID userId) {
        List<UUID> eventIds = eventLabelMappingDao.getLabelsOfEventsByUserId(userId)
            .stream()
            .filter(mapping -> mapping.getLabelIds().isEmpty())
            .map(EventLabelMapping::getEventId)
            .toList();

        return eventDao.getByIds(userId, eventIds)
            .stream()
            .map(event -> eventResponseMapper.toResponse(userId, event, List.of()))
            .toList();
    }

    public EventResponse getEvent(UUID userId, UUID eventId) {
        return objectQueryService.findEvent(userId, eventId)
            .map(event -> eventResponseMapper.toResponse(userId, event))
            .orElseThrow(() -> ExceptionFactory.notFound(userId + " has no access to event " + eventId + " or event does not exist."));
    }
}
