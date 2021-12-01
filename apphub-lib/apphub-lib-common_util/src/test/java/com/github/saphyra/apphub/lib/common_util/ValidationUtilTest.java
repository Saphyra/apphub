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

    enum TestEnum {
        ELEMENT
    }
}