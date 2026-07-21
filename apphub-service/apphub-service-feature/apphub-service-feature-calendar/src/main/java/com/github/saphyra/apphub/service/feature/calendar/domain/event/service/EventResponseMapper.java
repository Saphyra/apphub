package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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

    List<EventResponse> toResponse(UUID userId, List<BiWrapper<Event, Boolean>> events) {
        return events.stream()
            .map(bw -> toResponse(bw.getEntity1(), bw.getEntity2(), labelQueryService.getByEventId(userId, bw.getEntity1().getEventId())))
            .toList();
    }

    EventResponse toResponse(UUID userId, Event event, boolean shared) {
        return toResponse(event, shared, labelQueryService.getByEventId(userId, event.getEventId()));
    }

    EventResponse toResponse(Event event, boolean shared, Collection<LabelResponse> labels) {
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
            .shared(shared)
            .build();
    }
}
