package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelQueryService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventMapper {
    private final LabelQueryService labelQueryService;
    private final OccurrenceQueryService occurrenceQueryService;

     EventResponse toResponse(Event event) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .title(event.getTitle())
            .repetitionType(event.getRepetitionType())
            .repetitionData(event.getRepetitionData())
            .repeatForDays(event.getRepeatForDays())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .time(event.getTime())
            .title(event.getTitle())
            .content(event.getContent())
            .remindMeBeforeDays(event.getRemindMeBeforeDays())
            .occurrences(occurrenceQueryService.getOccurrences(event.getEventId()))
            .labels(labelQueryService.getByEventId(event.getEventId()))
            .build();
    }
}
