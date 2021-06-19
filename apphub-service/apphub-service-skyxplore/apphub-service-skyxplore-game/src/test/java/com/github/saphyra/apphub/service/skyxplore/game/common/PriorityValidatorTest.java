package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class PriorityValidatorTest {
    @InjectMocks
    private PriorityValidator underTest;

    @Test
    public void nullPriority() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "priority", "must not be null");
    }

    @Test
    public void priorityTooLow() {
        Throwable ex = catchThrowable(() -> underTest.validate(0));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too low");
    }

    @Test
    public void priorityTooHigh() {
        Throwable ex = catchThrowable(() -> underTest.validate(11));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too high");
    }

    @Test
    public void valid() {
        underTest.validate(4);
    }
}