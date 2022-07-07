package com.github.saphyra.apphub.service.diary.service.event.service.creation;

import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.diary.service.event.service.EventTitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
class CreateEventRequestValidator {
    private final EventTitleValidator eventTitleValidator;

    void validate(CreateEventRequest request) {
        eventTitleValidator.validate(request.getTitle());

        ValidationUtil.notNull(request.getReferenceDate(), "referenceDate");
        ValidationUtil.notNull(request.getDate(), "date");

        ValidationUtil.notNull(request.getRepetitionType(), "repetitionType");

        switch (request.getRepetitionType()) {
            case DAYS_OF_WEEK:
                ValidationUtil.notEmpty(request.getRepetitionDaysOfWeek(), "repetitionDaysOfWeek");
                break;
            case EVERY_X_DAYS:
                ValidationUtil.atLeast(request.getRepetitionDays(), 1, "repetitionDays");
                break;
            case ONE_TIME:
                break;
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled RepetitionType: " + request.getRepetitionType());
        }
    }
}
