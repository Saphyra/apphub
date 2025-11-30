package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventFactory {
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    public Event create(UUID userId, EventRequest request) {
        return Event.builder()
            .eventId(idGenerator.randomUuid())
            .userId(userId)
            .repetitionType(request.getRepetitionType())
            .repetitionData(objectMapper.writeValueAsString(request.getRepetitionData()))
            .repeatForDays(request.getRepeatForDays())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .time(request.getTime())
            .title(request.getTitle())
            .content(request.getContent())
            .remindMeBeforeDays(request.getRemindMeBeforeDays())
            .build();
    }
}
