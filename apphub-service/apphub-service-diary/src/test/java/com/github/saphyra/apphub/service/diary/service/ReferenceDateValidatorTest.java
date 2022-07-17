package com.github.saphyra.apphub.service.diary.service;

import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.catchThrowable;

public class ReferenceDateValidatorTest {
    private static final LocalDate DATE = LocalDate.now();

    private final ReferenceDateValidator underTest = new ReferenceDateValidator();

    @Test
    public void nullReferenceDate() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "referenceDate", "must not be null");
    }

    @Test
    public void nullMonth() {
        ReferenceDate referenceDate = ReferenceDate.builder()
            .day(DATE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(referenceDate));

        ExceptionValidator.validateInvalidParam(ex, "month", "must not be null");
    }

    @Test
    public void nullDay() {
        ReferenceDate referenceDate = ReferenceDate.builder()
            .month(DATE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(referenceDate));

        ExceptionValidator.validateInvalidParam(ex, "day", "must not be null");
    }

    @Test
    public void valid() {
        ReferenceDate referenceDate = ReferenceDate.builder()
            .month(DATE)
            .day(DATE)
            .build();

        underTest.validate(referenceDate);
    }
}