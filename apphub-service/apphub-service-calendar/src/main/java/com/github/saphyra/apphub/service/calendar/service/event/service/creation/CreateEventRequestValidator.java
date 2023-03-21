package com.github.saphyra.apphub.service.calendar.service.event.service.creation;

import com.github.saphyra.apphub.api.calendar.model.CreateEventRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar.service.event.service.EventTitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
class CreateEventRequestValidator {
    private final EventTitleValidator eventTitleValidator;
    private final ReferenceDateValidator referenceDateValidator;

    void validate(CreateEventRequest request) {
        eventTitleValidator.validate(request.getTitle());
        referenceDateValidator.validate(request.getReferenceDate());

        ValidationUtil.betweenInclusive(request.getRepeat(), 1, 365, "repeat");

        ValidationUtil.notNull(request.getDate(), "date");

        ValidationUtil.notNull(request.getRepetitionType(), "repetitionType");

        if (!isNull(request.getHours()) || !isNull(request.getMinutes())) {
            ValidationUtil.betweenInclusive(request.getHours(), 0, 23, "hours");
            ValidationUtil.betweenInclusive(request.getMinutes(), 0, 59, "minutes");
        }

        switch (request.getRepetitionType()) {
            case DAYS_OF_WEEK:
                ValidationUtil.notEmpty(request.getRepetitionDaysOfWeek(), "repetitionDaysOfWeek");
                break;
            case EVERY_X_DAYS:
                ValidationUtil.atLeast(request.getRepetitionDays(), 1, "repetitionDays");
                break;
            case DAYS_OF_MONTH:
                ValidationUtil.notEmpty(request.getRepetitionDaysOfMonth(), "repetitionDaysOfMonth");
                request.getRepetitionDaysOfMonth()
                    .forEach(integer -> {
                        ValidationUtil.atLeast(integer, 1, "repetitionDaysOfMonth");
                        ValidationUtil.maximum(integer, 31, "repetitionDaysOfMonth");
                    });
            case ONE_TIME:
                break;
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled RepetitionType: " + request.getRepetitionType());
        }
    }
}
