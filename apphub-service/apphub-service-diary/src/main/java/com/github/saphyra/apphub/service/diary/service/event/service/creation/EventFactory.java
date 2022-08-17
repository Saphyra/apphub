package com.github.saphyra.apphub.service.diary.service.event.service.creation;

import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EventFactory {
    private final IdGenerator idGenerator;
    private final ObjectMapperWrapper objectMapperWrapper;

    public Event create(UUID userId, CreateEventRequest request) {
        return Event.builder()
            .eventId(idGenerator.randomUuid())
            .userId(userId)
            .startDate(request.getDate())
            .repetitionType(request.getRepetitionType())
            .repetitionData(getRepetitionData(request))
            .title(request.getTitle())
            .content(request.getContent())
            .build();
    }

    private String getRepetitionData(CreateEventRequest request) {
        switch (request.getRepetitionType()) {
            case ONE_TIME:
                return null;
            case EVERY_X_DAYS:
                return String.valueOf(request.getRepetitionDays());
            case DAYS_OF_WEEK:
                return objectMapperWrapper.writeValueAsString(request.getRepetitionDaysOfWeek());
            case DAYS_OF_MONTH:
                return objectMapperWrapper.writeValueAsString(request.getRepetitionDaysOfMonth());
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled RepetitionType: " + request.getRepetitionType());
        }
    }
}
