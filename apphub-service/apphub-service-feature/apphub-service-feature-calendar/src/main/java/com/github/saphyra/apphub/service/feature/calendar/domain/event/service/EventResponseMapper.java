package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EventResponseMapper {
    private final ObjectMapper objectMapper;
    private final LabelQueryService labelQueryService;

    List<EventResponse> toResponse(UUID userId, List<Event> events) {
        return events.stream()
            .map(event -> toResponse(event, labelQueryService.getByEventId(userId, event.getEventId())))
            .toList();
    }

    EventResponse toResponse(UUID userId, Event event) {
        return toResponse(event, labelQueryService.getByEventId(userId, event.getEventId()));
    }

    EventResponse toResponse(Event event, Collection<LabelResponse> labels) {
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
            .autoDone(event.isAutoDone())
            .build();
    }
}
