package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RepetitionTypeConditionSelectorTest {
    private static final Object REPETITION_DATA = "repetition-data";

    @Mock
    private RepetitionTypeConditionFactory repetitionTypeConditionFactory;

    private RepetitionTypeConditionSelector underTest;

    @Mock
    private RepetitionTypeCondition repetitionTypeCondition;

    @BeforeEach
    void setUp() {
        underTest = new RepetitionTypeConditionSelector(List.of(repetitionTypeConditionFactory));
    }

    @Test
    void noMatchingConditionFactory() {
        assertThat(catchThrowable(() -> underTest.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void get() {
        given(repetitionTypeConditionFactory.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(repetitionTypeConditionFactory.create(REPETITION_DATA)).willReturn(repetitionTypeCondition);

        assertThat(underTest.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)).isEqualTo(repetitionTypeCondition);
    }
}