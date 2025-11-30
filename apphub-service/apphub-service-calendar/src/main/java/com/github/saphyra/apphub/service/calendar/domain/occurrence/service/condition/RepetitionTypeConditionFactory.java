package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceRepetitionTypeAware;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.DayOfWeek;
import java.util.Set;

interface RepetitionTypeConditionFactory extends OccurrenceRepetitionTypeAware {
    RepetitionTypeCondition create(Object repetitionData);

    @Component
    class OneTimeRepetitionTypeConditionFactory implements RepetitionTypeConditionFactory {
        @Override
        public RepetitionType getRepetitionType() {
            return RepetitionType.ONE_TIME;
        }

        @Override
        public RepetitionTypeCondition create(Object repetitionData) {
            return new OneTimeCondition();
        }
    }

    @Component
    class EveryXDaysRepetitionTypeConditionFactory implements RepetitionTypeConditionFactory {

        @Override
        public RepetitionType getRepetitionType() {
            return RepetitionType.EVERY_X_DAYS;
        }

        @Override
        public RepetitionTypeCondition create(Object repetitionData) {
            return new EveryXDaysCondition(Integer.parseInt(repetitionData.toString()));
        }
    }

    @Component
    @RequiredArgsConstructor
    class DaysOfWeekRepetitionTypeConditionFactory implements RepetitionTypeConditionFactory {
        private final ObjectMapper objectMapper;

        @Override
        public RepetitionType getRepetitionType() {
            return RepetitionType.DAYS_OF_WEEK;
        }

        @Override
        public RepetitionTypeCondition create(Object repetitionData) {
            TypeReference<Set<DayOfWeek>> typeReference = new TypeReference<>() {
            };
            Set<DayOfWeek> daysOfWeek = parseOrConvert(repetitionData, typeReference, objectMapper);

            return new DaysOfWeekCondition(daysOfWeek);
        }
    }

    @Component
    @RequiredArgsConstructor
    class DaysOfMonthRepetitionTypeConditionFactory implements RepetitionTypeConditionFactory {
        private final ObjectMapper objectMapper;

        @Override
        public RepetitionType getRepetitionType() {
            return RepetitionType.DAYS_OF_MONTH;
        }

        @Override
        public RepetitionTypeCondition create(Object repetitionData) {
            TypeReference<Set<Integer>> typeReference = new TypeReference<>() {
            };
            Set<Integer> daysOfMonth = parseOrConvert(repetitionData, typeReference, objectMapper);

            return new DaysOfMonthCondition(daysOfMonth);
        }
    }

    private static <T> T parseOrConvert(Object repetitionData, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        if (repetitionData instanceof String) {
            return objectMapper.readValue((String) repetitionData, typeReference);
        }
        return objectMapper.convertValue(repetitionData, typeReference);
    }
}
