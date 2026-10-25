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
    private static final String VALUE = "value";

    @Test
    public void notNull_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notNull(null, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void notNull() {
        ValidationUtil.notNull(1, FIELD);
    }

    @Test
    public void notBlank_blank() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notBlank(" ", FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    public void notBlank() {
        ValidationUtil.notBlank("1", FIELD);
    }

    @Test
    public void enumElementExists_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.convertToEnumChecked((String) null, TestEnum::valueOf, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void enumElementExists_elementDoesNotExist() {
        Throwable ex = catchThrowable(() -> ValidationUtil.convertToEnumChecked("asd", TestEnum::valueOf, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.INVALID_VALUE);
    }

    @Test
    public void enumElementExists() {
        ValidationUtil.convertToEnumChecked(TestEnum.ELEMENT.name(), TestEnum::valueOf, FIELD);
    }

    @Test
    public void minLength_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.minLength(null, 1, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void minLength_tooShort() {
        Throwable ex = catchThrowable(() -> ValidationUtil.minLength("asd", 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_SHORT);
    }

    @Test
    public void maxLength_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maximum(null, 1, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void maxLength_tooLong() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maxLength("asd", 2, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_LONG);
    }

    @Test
    public void minLength() {
        ValidationUtil.minLength("asd", 3, FIELD);
    }

    @Test
    public void atLeast_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast((Integer) null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void atLeast_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(9, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_LOW);
    }

    @Test
    public void atLeast_long() {
        ValidationUtil.atLeast(1L, 1, FIELD);
    }

    @Test
    public void atLeast_long_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast((Long) null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void atLeast_long_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(9L, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_LOW);
    }

    @Test
    public void atLeast() {
        ValidationUtil.atLeast(1, 1, FIELD);
    }

    @Test
    public void maximum_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maximum(null, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void maximum_tooHigh() {
        Throwable ex = catchThrowable(() -> ValidationUtil.maximum(11, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_HIGH);
    }

    @Test
    public void maximum() {
        ValidationUtil.maximum(10, 10, FIELD);
    }

    @Test
    public void between_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(null, 5d, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void between_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(4, 5, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_LOW);
    }

    @Test
    public void between_tooHigh() {
        Throwable ex = catchThrowable(() -> ValidationUtil.betweenInclusive(11, 5, 10, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_HIGH);
    }

    @Test
    public void between() {
        ValidationUtil.betweenInclusive(8, 5, 10, FIELD);
    }

    @Test
    public void notEmpty_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notEmpty((Collection<?>) null, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void notEmpty_empty() {
        Throwable ex = catchThrowable(() -> ValidationUtil.notEmpty(Collections.emptyList(), FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_EMPTY);
    }

    @Test
    public void notEmpty() {
        ValidationUtil.notEmpty(List.of("asd"), FIELD);
    }

    @Test
    public void contains_error() {
        Throwable ex = catchThrowable(() -> ValidationUtil.contains("asd", List.of("dsa"), FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, String.format(ValidationUtil.MUST_BE_ONE_OF, List.of("dsa")));
    }

    @Test
    public void contains() {
        ValidationUtil.contains("asd", List.of("asd"), FIELD);
    }

    @Test
    void exactLengthTest_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.length(null, 3, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void exactLengthTest_differentLength() {
        Throwable ex = catchThrowable(() -> ValidationUtil.length("d", 3, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, String.format(ValidationUtil.MUST_BE_LENGTH, 3));
    }

    @Test
    void exactLengthTest() {
        ValidationUtil.length("asd", 3, FIELD);
    }

    @Test
    void atLeastInclusive_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeastExclusive(null, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void atLeastInclusive_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeastExclusive(2d, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_LOW);
    }

    @Test
    void atLeastInclusive() {
        ValidationUtil.atLeastExclusive(3d, 2d, FIELD);
    }

    @Test
    void atLeastDouble_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(null, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void atLeastDouble_tooLow() {
        Throwable ex = catchThrowable(() -> ValidationUtil.atLeast(1d, 2d, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.TOO_LOW);
    }

    @Test
    void atLeastDouble() {
        ValidationUtil.atLeast(2d, 2d, FIELD);
    }

    @Test
    void parse_null() {
        Throwable ex = catchThrowable(() -> ValidationUtil.parse(null, Function.identity(), FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void parse_failed() {
        Throwable ex = catchThrowable(() -> ValidationUtil.parse("asd", o -> {
            throw new RuntimeException();
        }, FIELD));

        ExceptionValidator.validateInvalidParam(ex, FIELD, ValidationUtil.FAILED_TO_PARSE);
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

        ExceptionValidator.validateInvalidParam(ex, FIELD, String.format(ValidationUtil.MUST_BE, "a"));
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
        ExceptionValidator.validateInvalidParam(catchThrowable(() -> ValidationUtil.notZero(null, FIELD)), FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void notZero_zero() {
        ExceptionValidator.validateInvalidParam(catchThrowable(() -> ValidationUtil.notZero(0, FIELD)), FIELD, ValidationUtil.MUST_NOT_BE_ZERO);
    }

    @Test
    void notEmpty_map_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.notEmpty((Map<String, String>) null, FIELD), FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void notEmpty_map_empty() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.notEmpty(Map.of(), FIELD), FIELD, ValidationUtil.MUST_NOT_BE_EMPTY);
    }

    @Test
    void notEmpty_map() {
        ValidationUtil.notEmpty(Map.of(ValidationUtil.KEY, VALUE), FIELD);
    }

    @Test
    void containsKey_nullKey() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsKey(null, Map.of(ValidationUtil.KEY, VALUE), FIELD), ValidationUtil.KEY, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void containsKey_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsKey(ValidationUtil.KEY, null, FIELD), FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void containsKey_doesNotContain() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsKey(ValidationUtil.KEY, Map.of(VALUE, ValidationUtil.KEY), FIELD), FIELD, ValidationUtil.INVALID_VALUE);
    }

    @Test
    void containsKey() {
        ValidationUtil.containsKey(ValidationUtil.KEY, Map.of(ValidationUtil.KEY, VALUE), FIELD);
    }

    @Test
    void mapDoesNotContainNull_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.doesNotContainNull((Map<Object, Object>) null, FIELD), FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void mapDoesNotContainNull_containsNull() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.doesNotContainNull(CollectionUtils.singleValueMap(ValidationUtil.KEY, null), FIELD), String.format(ValidationUtil.FIELD_PATH, FIELD, ValidationUtil.KEY), ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void mapDoesNotContainNull() {
        ValidationUtil.doesNotContainNull(Map.of(ValidationUtil.KEY, VALUE), FIELD);
    }

    @Test
    void collectionDoesNotContainNull_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.doesNotContainNull((Collection<Object>) null, FIELD), FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void collectionDoesNotContainNull_containsNull() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.doesNotContainNull(CollectionUtils.toList("a", null, "b"), FIELD), FIELD, ValidationUtil.MUST_NOT_CONTAIN_NULL_VALUES);
    }

    @Test
    void collectionDoesNotContainNull() {
        ValidationUtil.doesNotContainNull(CollectionUtils.toList("a", "b"), FIELD);
    }

    @Test
    void containsAll_null() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsAll(null, () -> List.of("a"), FIELD), FIELD, ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void containsAll_containsNull() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsAll(CollectionUtils.toList("a", null), () -> List.of("a"), FIELD), FIELD, ValidationUtil.MUST_NOT_CONTAIN_NULL_VALUES);
    }

    @Test
    void containsAll_unsupportedValue() {
        ExceptionValidator.validateInvalidParam(() -> ValidationUtil.containsAll(List.of("a", "b"), () -> List.of("a"), FIELD), FIELD, String.format(ValidationUtil.UNSUPPORTED_VALUES, List.of("b")));
    }

    @Test
    void containsAll() {
        ValidationUtil.containsAll(List.of("a", "b"), () -> List.of("a", "b", "c"), FIELD);
    }

    enum TestEnum {
        ELEMENT
    }
}