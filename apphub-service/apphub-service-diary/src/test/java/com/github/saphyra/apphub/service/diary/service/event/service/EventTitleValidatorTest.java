package com.github.saphyra.apphub.service.diary.service.event.service;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.catchThrowable;

public class EventTitleValidatorTest {
    private final EventTitleValidator underTest = new EventTitleValidator();

    @Test
    public void nullTitle() {
        Throwable ex = catchThrowable(() -> underTest.validate(" "));

        ExceptionValidator.validateInvalidParam(ex, "title", "must not be null or blank");
    }

    @Test
    public void valid() {
        underTest.validate("a");
    }
}