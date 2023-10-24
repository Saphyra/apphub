package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
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
        Throwable ex = catchThrowable(() -> ValidationUtil.convertToEnumChecked((String) null, TestEnum::valueOf, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void enumElementExists_elementDoesNotExist() {
        Throwable ex = catchThrowable(() -> ValidationUtil.convertToEnumChecked("asd", TestEnum::valueOf, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "invalid value");
    }

    @Test
    public void enumElementExists() {
        ValidationUtil.convertToEnumChecked(TestEnum.ELEMENT.name(), TestEnum::valueOf, FIELD);
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
    public void maxLength_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maximum(null, 1, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void maxLength_tooLong() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maxLength("asd", 2, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too long");
    }

    @Test
    public void minLength() {
        ValidationUtil.minLength("asd", 3, FIELD);
    }

    @Test
    public void atLeast_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast((Integer) null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void atLeast_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(9, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too low");
    }

    @Test
    public void atLeast_long() {
        ValidationUtil.atLeast(1L, 1, FIELD);
    }

    @Test
    public void atLeast_long_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast((Long) null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void atLeast_long_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(9L, 10, FIELD));

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

    @Test
    public void notEmpty_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notEmpty(null, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    public void notEmpty_empty() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notEmpty(Collections.emptyList(), FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be empty");
    }

    @Test
    public void notEmpty() {
        ValidationUtil.notEmpty(List.of("asd"), FIELD);
    }

    @Test
    public void contains_error() {
        Throwable ex = catchThrowable(() -> ValidationUtil.contains("asd", List.of("dsa"), FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, String.format("must be one of %s", List.of("dsa")));
    }

    @Test
    public void contains() {
        ValidationUtil.contains("asd", List.of("asd"), FIELD);
    }

    @Test
    void exactLengthTest_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.length(null, 3, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    void exactLengthTest_differentLength() {
        Throwable ex = catchThrowable(() -> ValidationUtil.length("d", 3, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must be 3 long");
    }

    @Test
    void exactLengthTest() {
        ValidationUtil.length("asd", 3, FIELD);
    }

    @Test
    void atLeastInclusive_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeastInclusive(null, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    void atLeastInclusive_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeastInclusive(2d, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too low");
    }

    @Test
    void atLeastInclusive() {
        ValidationUtil.atLeastInclusive(3d, 2d, FIELD);
    }

    @Test
    void atLeastDouble_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(null, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    void atLeastDouble_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(1d, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too low");
    }

    @Test
    void atLeastDouble() {
        ValidationUtil.atLeast(2d, 2d, FIELD);
    }

    @Test
    void parse_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.parse(null, Function.identity(), FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    void parse_failed() {
        Throwable ex = catchThrowable(() -> ValidationUtil.parse("asd", o -> {
            throw new RuntimeException();
        }, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "failed to parse");
    }

    @Test
    void parse() {
        Function<Object, Integer> parser = o -> ((String) o).length();

        assertThat(ValidationUtil.parse("asd", parser, FIELD)).isEqualTo(3);
    }

    @Test
    void equals(){
        ValidationUtil.equals("a", "a", FIELD);
    }

    @Test
    void equals_doesNotEqual(){
        Throwable ex = catchThrowable(() -> ValidationUtil.equals("b", "a", FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must be a");
    }

    enum TestEnum {
        ELEMENT
    }
}