package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
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

import static com.github.saphyra.apphub.service.feature.calendar.common.CalendarUtils.mask;

@Component
@RequiredArgsConstructor
@Slf4j
class EventResponseMapper {
    private final ObjectMapper objectMapper;
    private final LabelQueryService labelQueryService;

    List<EventResponse> toResponse(UUID userId, List<Event> events) {
        return events.stream()
            .map(event -> toResponse(userId, event, labelQueryService.getByEventId(event.getUserId(), event.getEventId())))
            .toList();
    }

    EventResponse toResponse(UUID userId, Event event) {
        return toResponse(userId, event, labelQueryService.getByEventId(event.getUserId(), event.getEventId()));
    }

    //TODO unit test masked data
    EventResponse toResponse(UUID userId, Event event, Collection<LabelResponse> labels) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .repetitionType(event.getRepetitionType())
            .repetitionData(Optional.ofNullable(event.getRepetitionData()).map(repetitionData -> objectMapper.readValue(repetitionData, Object.class)).orElse(null))
            .repeatForDays(event.getRepeatForDays())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .time(event.getTime())
            .title(mask(event.isMasked(), event.getTitle(), Constants.QUESTION_MARK))
            .content(mask(event.isMasked(), event.getContent(), Constants.EMPTY_STRING))
            .remindMeBeforeDays(mask(event.isMasked(), event.getRemindMeBeforeDays(), null))
            .labels(mask(event.isMasked(), labels, List.of()))
            .archived(mask(event.isMasked(), event.isArchived(), null))
            .autoDone(mask(event.isMasked(), event.isAutoDone(), null))
            .shared(!event.getUserId().equals(userId))
            .build();
    }
}
