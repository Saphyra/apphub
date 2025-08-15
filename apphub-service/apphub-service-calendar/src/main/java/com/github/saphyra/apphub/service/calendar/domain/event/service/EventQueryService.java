package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventQueryService {
    private final EventDao eventDao;
    private final EventLabelMappingService eventLabelMappingService;
    private final EventMapper eventMapper;

    public List<EventResponse> getEvents(UUID userId, UUID labelId) {
        return eventDao.getByUserId(userId)
            .stream()
            .filter(event -> isNull(labelId) || eventLabelMappingService.hasLabel(event.getEventId(), labelId))
            .map(eventMapper::toResponse)
            .collect(Collectors.toList());
    }

    public EventResponse getEvent(UUID eventId) {
        return eventMapper.toResponse(eventDao.findByIdValidated(eventId));
    }
}
