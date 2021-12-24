package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.catchThrowable;

public class ValidationUtilTest {
    private static final String FIELD = "field";

    @Test
    public void notNull_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notNull(null, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void notNull() {
        ValidationUtil.notNull(1, FIELD);
    }

    @Test
    public void notBlank_blank() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notBlank(" ", FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null or blank");
    }

    @Test
    public void notBlank() {
        ValidationUtil.notBlank("1", FIELD);
    }

    @Test
    public void enumElementExists_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.enumElementExists((String) null, TestEnum::valueOf, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void enumElementExists_elementDoesNotExist() {
        Throwable ex = catchThrowable(() -> ValidationUtil.enumElementExists("asd", TestEnum::valueOf, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "invalid value");
    }

    @Test
    public void enumElementExists() {
        ValidationUtil.enumElementExists(TestEnum.ELEMENT.name(), TestEnum::valueOf, FIELD);
    }

    @Test
    public void minLength_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.minLength(null, 1, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void minLength_tooShort() {
        Throwable ex = catchThrowable(() -> ValidationUtil.minLength("asd", 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too short");
    }

    @Test
    public void minLength() {
        ValidationUtil.minLength("asd", 3, FIELD);
    }

    @Test
    public void atLeast_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void atLeast_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(9, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too low");
    }

    @Test
    public void atLeast() {
        ValidationUtil.atLeast(1, 1, FIELD);
    }

    @Test
    public void maximum_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maximum(null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void maximum_tooHigh() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maximum(11, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too high");
    }

    @Test
    public void maximum() {
        ValidationUtil.maximum(10, 10, FIELD);
    }

    @Test
    public void between_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(null, 5, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void between_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(4, 5, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too low");
    }

    @Test
    public void between_tooHigh() {
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(11, 5, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too high");
    }

    @Test
    public void between() {
        ValidationUtil.betweenInclusive(8, 5, 10, FIELD);
    }

    enum TestEnum {
        ELEMENT
    }
}