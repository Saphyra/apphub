package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventRequestValidator {
    private final ObjectMapperWrapper objectMapperWrapper;

    void validate(EventRequest request) {
        ValidationUtil.notNull(request.getRepetitionType(), "repetitionType");
        validateRepetitionData(request.getRepetitionType(), request.getRepetitionData());
        ValidationUtil.notNull(request.getRepeatForDays(), "repeatForDays");
        ValidationUtil.notNull(request.getStartDate(), "startDate");
        ValidationUtil.notNull(request.getEndDate(), "endDate");
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw ExceptionFactory.invalidParam("startDate", "startDate cannot be after endDate");
        }
        ValidationUtil.notBlank(request.getTitle(), "title");
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.notNull(request.getRemindMeBeforeDays(), "remindMeBeforeDays");
        ValidationUtil.notNull(request.getLabels(), "labels");
    }

    private void validateRepetitionData(RepetitionType repetitionType, String repetitionData) {
        switch (repetitionType) {
            case ONE_TIME:
                break;

            case EVERY_X_DAYS:
                ValidationUtil.parse(repetitionData, o -> Integer.parseInt(o.toString()), "repetitionData");
                break;

            case DAYS_OF_WEEK:
                TypeReference<List<DayOfWeek>> daysOfWeekTypeReference = new TypeReference<>() {
                };
                ValidationUtil.parse(repetitionData, o -> objectMapperWrapper.convertValue(o, daysOfWeekTypeReference), "repetitionData");
                break;

            case DAYS_OF_MONTH:
                TypeReference<List<Integer>> daysOfMonthTypeReference = new TypeReference<>() {
                };
                List<Integer> values = ValidationUtil.parse(repetitionData, o -> objectMapperWrapper.convertValue(o, daysOfMonthTypeReference), "repetitionData");
                values.forEach(integer -> {
                    ValidationUtil.atLeast(integer, 1, "repetitionData");
                    ValidationUtil.maximum(integer, 31, "repetitionData");
                });
                break;

            default:
                throw ExceptionFactory.invalidParam("repetitionType", "Unhandled RepetitionType: " + repetitionType);
        }
    }
}
