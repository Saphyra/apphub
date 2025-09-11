package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RepetitionTypeConditionFactoryTest {
    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    @Test
    void oneTime() {
        RepetitionTypeConditionFactory underTest = new RepetitionTypeConditionFactory.OneTimeRepetitionTypeConditionFactory();

        assertThat(underTest.create(null)).isInstanceOf(OneTimeCondition.class);
    }

    @Test
    void everyXDays() {
        RepetitionTypeConditionFactory underTest = new RepetitionTypeConditionFactory.EveryXDaysRepetitionTypeConditionFactory();

        assertThat(underTest.create("5")).isInstanceOf(EveryXDaysCondition.class);
        assertThat(underTest.create(5)).isInstanceOf(EveryXDaysCondition.class);
    }

    @Test
    void daysOfWeek() {
        RepetitionTypeConditionFactory underTest = new RepetitionTypeConditionFactory.DaysOfWeekRepetitionTypeConditionFactory(objectMapperWrapper);

        assertThat(underTest.create("[\"MONDAY\"]")).isInstanceOf(DaysOfWeekCondition.class);
        assertThat(underTest.create(List.of(DayOfWeek.MONDAY))).isInstanceOf(DaysOfWeekCondition.class);
    }

    @Test
    void daysOfMonth() {
        RepetitionTypeConditionFactory underTest = new RepetitionTypeConditionFactory.DaysOfMonthRepetitionTypeConditionFactory(objectMapperWrapper);

        assertThat(underTest.create("[1,2,3]")).isInstanceOf(DaysOfMonthCondition.class);
        assertThat(underTest.create(List.of(1, 2, 3))).isInstanceOf(DaysOfMonthCondition.class);
    }
}