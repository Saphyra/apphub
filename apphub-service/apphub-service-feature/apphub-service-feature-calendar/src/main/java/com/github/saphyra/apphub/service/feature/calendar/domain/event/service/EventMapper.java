package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventMapper {
    private final ObjectMapper objectMapper;
    private final EventLabelMappingDao eventLabelMappingDao;

    List<EventResponse> toResponse(UUID userId, List<Event> events) {
        List<UUID> eventIds = events.stream()
            .map(Event::getEventId)
            .toList();

        Map<UUID, List<UUID>> labelMapping = eventLabelMappingDao.getByEventIds(userId, eventIds);

        return events.stream()
            .map(event -> toResponse(event, labelMapping.get(event.getEventId())))
            .toList();
    }

    EventResponse toResponse(Event event) {
        return toResponse(event, eventLabelMappingDao.getByEventId(event.getUserId(), event.getEventId()));
    }

    EventResponse toResponse(Event event, List<UUID> labels) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .repetitionType(event.getRepetitionType())
            .repetitionData(Optional.ofNullable(event.getRepetitionData()).map(repetitionData -> objectMapper.readValue(repetitionData, Object.class)).orElse(null))
            .repeatForDays(event.getRepeatForDays())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .time(event.getTime())
            .title(event.getTitle())
            .content(event.getContent())
            .remindMeBeforeDays(event.getRemindMeBeforeDays())
            .labels(labels)
            .archived(event.isArchived())
            .build();
    }
}
