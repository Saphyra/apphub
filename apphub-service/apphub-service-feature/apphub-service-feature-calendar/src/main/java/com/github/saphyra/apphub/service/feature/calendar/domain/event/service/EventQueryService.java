package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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

    public List<EventResponse> getEvents(UUID userId, UUID labelId) {
        List<Event> events;
        if (isNull(labelId)) {
            events = eventDao.getByUserId(userId);
        } else {
            List<UUID> eventIds = eventLabelMappingDao.getEventsOfLabel(userId, labelId);
            events = eventDao.getByIds(userId, eventIds);
        }

        return eventResponseMapper.toResponse(userId, events);
    }

    public List<EventResponse> getLabellessEvents(UUID userId) {
        List<UUID> eventIds =  eventLabelMappingDao.getLabelsOfEventsByUserId(userId)
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().isEmpty())
            .map(Map.Entry::getKey)
            .toList();

        return eventDao.getByIds(userId, eventIds)
            .stream()
            .map(event -> eventResponseMapper.toResponse(event, List.of()))
            .toList();
    }

    public EventResponse getEvent(UUID userId, UUID eventId) {
        return eventResponseMapper.toResponse(eventDao.findByIdValidated(userId, eventId));
    }
}
