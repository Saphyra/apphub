package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventMapper {
    private final ObjectMapperWrapper objectMapperWrapper;

     EventResponse toResponse(Event event) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .title(event.getTitle())
            .repetitionType(event.getRepetitionType())
            .repetitionData(objectMapperWrapper.readValue(event.getRepetitionData(), Object.class))
            .repeatForDays(event.getRepeatForDays())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .time(event.getTime())
            .title(event.getTitle())
            .content(event.getContent())
            .remindMeBeforeDays(event.getRemindMeBeforeDays())
            .build();
    }
}
