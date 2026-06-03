package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.config.CalendarParams;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EventRequestValidator {
    private final ObjectMapper objectMapper;
    private final CalendarParams calendarParams;
    private final LabelDao labelDao;

    public void validateEdit(UUID userId, EventRequest request) {
        validate(userId, request);

        ValidationUtil.notNull(request.getArchived(), "archived");
    }

    void validate(UUID userId, EventRequest request) {
        ValidationUtil.notNull(request.getRepetitionType(), "repetitionType");
        validateRepetitionData(request.getRepetitionType(), request.getRepetitionData());

        ValidationUtil.atLeast(request.getRepeatForDays(), 1, "repeatForDays");
        ValidationUtil.notNull(request.getStartDate(), "startDate");

        if (request.getRepetitionType() != RepetitionType.ONE_TIME) {
            if (isNull(request.getEndDate())) {
                request.setEndDate(request.getStartDate().plusDays(calendarParams.getMaxEventDurationDays()));
            }
            validateDates(request.getStartDate(), request.getEndDate());
        }

        ValidationUtil.notBlank(request.getTitle(), "title");
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.atLeast(request.getRemindMeBeforeDays(), 0, "remindMeBeforeDays");

        ValidationUtil.containsAll(request.getLabels(), () -> labelDao.getByLabelIds(userId, request.getLabels()).stream().map(Label::getLabelId).toList(), "labels");
    }

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        ValidationUtil.notNull(endDate, "endDate");
        if (startDate.isAfter(endDate)) {
            throw ExceptionFactory.invalidParam("startDate", "startDate cannot be after endDate");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > calendarParams.getMaxEventDurationDays()) {
            throw ExceptionFactory.invalidParam("eventDuration", "too long");
        }
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
                TypeReference<HashSet<DayOfWeek>> daysOfWeekTypeReference = new TypeReference<>() {
                };
                Set<DayOfWeek> dayOfWeeks = ValidationUtil.parse(repetitionData, o -> objectMapper.convertValue(o, daysOfWeekTypeReference), "repetitionData");
                ValidationUtil.doesNotContainNull(dayOfWeeks, "repetitionData");
                ValidationUtil.notEmpty(dayOfWeeks, "repetitionData");
                break;

            case DAYS_OF_MONTH:
                TypeReference<List<Integer>> daysOfMonthTypeReference = new TypeReference<>() {
                };
                List<Integer> values = ValidationUtil.parse(repetitionData, o -> objectMapper.convertValue(o, daysOfMonthTypeReference), "repetitionData");
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
