package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ValidationUtilTest {
    private static final String FIELD = "field";
    private static final String KEY = "key";
    private static final String VALUE = "value";

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
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(null, 5d, 10, FIELD));

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
        Throwable ex = catchThrowable(() -> ValidationUtil.notEmpty((Collection<?>) null, FIELD));

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

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must be 3 character(s) long");
    }

    @Test
    void exactLengthTest() {
        ValidationUtil.length("asd", 3, FIELD);
    }

    @Test
    void atLeastInclusive_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeastExclusive(null, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must not be null");
    }

    @Test
    void atLeastInclusive_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeastExclusive(2d, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "too low");
    }

    @Test
    void atLeastInclusive() {
        ValidationUtil.atLeastExclusive(3d, 2d, FIELD);
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
    void equals() {
        ValidationUtil.equals("a", "a", FIELD);
    }

    @Test
    void equals_doesNotEqual() {
        Throwable ex = catchThrowable(() -> ValidationUtil.equals("b", "a", FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, "must be a");
    }

    @Test
    void notZero_positive() {
        ValidationUtil.notZero(1, FIELD);
    }

    @Test
    void notZero_negative() {
        ValidationUtil.notZero(-1, FIELD);
    }

    @Test
    void notZero_null() {
        ExceptionValidator.validateInvalidParam(catchThrowable(() -> ValidationUtil.notZero(null, FIELD)), FIELD, "must not be null");
    }

    @Test
    void notZero_zero() {
        ExceptionValidator.validateInvalidParam(catchThrowable(() -> ValidationUtil.notZero(0, FIELD)), FIELD, "must not be zero");
    }

    @Test
    void notEmpty_map_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.notEmpty((Map<String, String>) null, FIELD), FIELD, "must not be null");
    }

    @Test
    void notEmpty_map_empty() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.notEmpty(Map.of(), FIELD), FIELD, "must not be empty");
    }

    @Test
    void notEmpty_map() {
        ValidationUtil.notEmpty(Map.of(KEY, VALUE), FIELD);
    }

    @Test
    void containsKey_nullKey() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsKey(null, Map.of(KEY, VALUE), FIELD), "key", "must not be null");
    }

    @Test
    void containsKey_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsKey(KEY, null, FIELD), FIELD, "must not be null");
    }

    @Test
    void containsKey_doesNotContain() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsKey(KEY, Map.of(VALUE, KEY), FIELD), FIELD, "invalid value");
    }

    @Test
    void containsKey() {
        ValidationUtil.containsKey(KEY, Map.of(KEY, VALUE), FIELD);
    }

    @Test
    void doesNotContainNull_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.doesNotContainNull(null, FIELD), FIELD, "must not be null");
    }

    @Test
    void doesNotContainNull_containsNull() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.doesNotContainNull(CollectionUtils.singleValueMap(KEY, null), FIELD), "%s.%s".formatted(FIELD, KEY), "must not be null");
    }

    @Test
    void doesNotContainNull() {
        ValidationUtil.doesNotContainNull(Map.of(KEY, VALUE), FIELD);
    }

    enum TestEnum {
        ELEMENT
    }
}