package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
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
            .autoDone(request.getAutoDone())
            .build();
    }

    public Event dummyEvent(UUID userId, UUID eventId) {
        return Event.builder()
            .eventId(eventId)
            .userId(userId)
            .title("?")
            .repetitionType(RepetitionType.ONE_TIME)
            .repeatForDays(1)
            .startDate(LocalDate.of(1970, 1, 1))
            .content("")
            .build();
    }
}
