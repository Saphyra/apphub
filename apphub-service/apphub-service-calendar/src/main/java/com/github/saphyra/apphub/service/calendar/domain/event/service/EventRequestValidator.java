package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar.config.CalendarParams;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
class EventRequestValidator {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final CalendarParams calendarParams;
    private final LabelDao labelDao;

    void validate(EventRequest request) {
        ValidationUtil.notNull(request.getRepetitionType(), "repetitionType");
        validateRepetitionData(request.getRepetitionType(), request.getRepetitionData());

        ValidationUtil.atLeast(request.getRepeatForDays(), 1, "repeatForDays");
        ValidationUtil.notNull(request.getStartDate(), "startDate");

        if (request.getRepetitionType() != RepetitionType.ONE_TIME) {
            ValidationUtil.notNull(request.getEndDate(), "endDate");
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw ExceptionFactory.invalidParam("startDate", "startDate cannot be after endDate");
            }
            if (ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) > calendarParams.getMaxEventDurationDays()) {
                throw ExceptionFactory.invalidParam("eventDuration", "too long");
            }
        }

        ValidationUtil.notBlank(request.getTitle(), "title");
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.atLeast(request.getRemindMeBeforeDays(), 0, "remindMeBeforeDays");
        ValidationUtil.doesNotContainNull(request.getLabels(), "labels");

        request.getLabels()
            .forEach(labelId -> {
                if (!labelDao.existsById(labelId)) {
                    throw ExceptionFactory.invalidParam("labelId", "does not exist");
                }
            });
    }

    private void validateRepetitionData(RepetitionType repetitionType, Object repetitionData) {
        switch (repetitionType) {
            case ONE_TIME:
                break;

            case EVERY_X_DAYS:
                Integer days = ValidationUtil.parse(repetitionData, o -> Integer.parseInt(o.toString()), "repetitionData");
                ValidationUtil.atLeast(days, 1, "repetitionData");
                break;

            case DAYS_OF_WEEK:
                TypeReference<Set<DayOfWeek>> daysOfWeekTypeReference = new TypeReference<>() {
                };
                Set<DayOfWeek> dayOfWeeks = ValidationUtil.parse(repetitionData, o -> objectMapperWrapper.convertValue(o, daysOfWeekTypeReference), "repetitionData");
                ValidationUtil.doesNotContainNull(dayOfWeeks, "repetitionData");
                ValidationUtil.notEmpty(dayOfWeeks, "repetitionData");
                break;

            case DAYS_OF_MONTH:
                TypeReference<List<Integer>> daysOfMonthTypeReference = new TypeReference<>() {
                };
                List<Integer> values = ValidationUtil.parse(repetitionData, o -> objectMapperWrapper.convertValue(o, daysOfMonthTypeReference), "repetitionData");
                ValidationUtil.notEmpty(values, "repetitionData");
                ValidationUtil.doesNotContainNull(values, "repetitionData");
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
