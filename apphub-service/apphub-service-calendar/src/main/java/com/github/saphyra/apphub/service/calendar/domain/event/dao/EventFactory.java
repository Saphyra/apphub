package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventFactory {
    private final IdGenerator idGenerator;
    private final ObjectMapperWrapper objectMapperWrapper;

    public Event create(UUID userId, EventRequest request) {
        return Event.builder()
            .eventId(idGenerator.randomUuid())
            .userId(userId)
            .repetitionType(request.getRepetitionType())
            .repetitionData(objectMapperWrapper.writeValueAsString(request.getRepetitionData()))
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
