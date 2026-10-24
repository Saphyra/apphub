package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ValidationUtil {
    public static final String MUST_NOT_BE_NULL = "must not be null";
    public static final String MUST_NOT_BE_NULL_OR_BLANK = "must not be null or blank";
    public static final String INVALID_VALUE = "invalid value";
    public static final String MUST_BE_LENGTH = "must be %s character(s) long";
    public static final String TOO_SHORT = "too short";
    public static final String TOO_LONG = "too long";
    public static final String TOO_LOW = "too low";
    public static final String TOO_HIGH = "too high";
    public static final String MUST_NOT_BE_EMPTY = "must not be empty";
    public static final String MUST_BE_ONE_OF = "must be one of %s";
    public static final String KEY = "key";
    public static final String ALL_VALUES_ARE_NULL = "all values are null";
    public static final String FAILED_TO_PARSE = "failed to parse";
    public static final String MUST_BE = "must be %s";
    public static final String MUST_NOT_BE_ZERO = "must not be zero";
    public static final String FIELD_PATH = "%s.%s";
    public static final String MUST_NOT_CONTAIN_NULL_VALUES = "must not contain null values";
    public static final String UNSUPPORTED_VALUES = "Unsupported values: %s";
    public static final String COMMA_SEPARATOR = ", ";

    public static void notNull(Object value, String fieldName) {
        if (isNull(value)) {
            throw ExceptionFactory.invalidParam(fieldName, MUST_NOT_BE_NULL);
        }
    }

    public static void notBlank(String value, String fieldName) {
        if (isBlank(value)) {
            throw ExceptionFactory.invalidParam(fieldName, MUST_NOT_BE_NULL_OR_BLANK);
        }
    }

    public static <T, R> R convertToEnumChecked(T value, Function<T, R> mapper, String fieldName) {
        try {
            notNull(value, fieldName);
            return mapper.apply(value);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.invalidParam(fieldName, INVALID_VALUE);
        }
    }

    public static void length(String value, int length, String field) {
        notNull(value, field);
        if (value.length() != length) {
            throw ExceptionFactory.invalidParam(field, String.format(MUST_BE_LENGTH, length));
        }
    }

    public static void minLength(String value, int minLength, String field) {
        notNull(value, field);
        if (value.length() < minLength) {
            throw ExceptionFactory.invalidParam(field, TOO_SHORT);
        }
    }

    public static void maxLength(String value, int maxLength, String field) {
        notNull(value, field);
        if (value.length() > maxLength) {
            throw ExceptionFactory.invalidParam(field, TOO_LONG);
        }
    }

    public static void atLeastExclusive(Double value, double minValue, String field) {
        notNull(value, field);
        if (value <= minValue) {
            throw ExceptionFactory.invalidParam(field, TOO_LOW);
        }
    }

    public static void atLeast(Double value, double minValue, String field) {
        notNull(value, field);
        if (value < minValue) {
            throw ExceptionFactory.invalidParam(field, TOO_LOW);
        }
    }

    public static void atLeast(Integer value, int minValue, String field) {
        notNull(value, field);
        if (value < minValue) {
            throw ExceptionFactory.invalidParam(field, TOO_LOW);
        }
    }

    public static void atLeast(Long value, int minValue, String field) {
        notNull(value, field);
        if (value < minValue) {
            throw ExceptionFactory.invalidParam(field, TOO_LOW);
        }
    }

    public static void maximum(Integer value, Integer max, String field) {
        notNull(value, field);
        if (value > max) {
            throw ExceptionFactory.invalidParam(field, TOO_HIGH);
        }
    }

    public static void maximum(Double value, double max, String field) {
        notNull(value, field);
        if (value > max) {
            throw ExceptionFactory.invalidParam(field, TOO_HIGH);
        }
    }

    public static void maximum(Long value, Long max, String field) {
        notNull(value, field);
        if (value > max) {
            throw ExceptionFactory.invalidParam(field, TOO_HIGH);
        }
    }

    public static void betweenInclusive(Integer value, int min, int max, String field) {
        notNull(value, field);
        atLeast(value, min, field);
        maximum(value, max, field);
    }

    public static void betweenInclusive(Double value, double min, double max, String field) {
        notNull(value, field);
        atLeast(value, min, field);
        maximum(value, max, field);
    }

    public static void notEmpty(Collection<?> collection, String field) {
        notNull(collection, field);
        if (collection.isEmpty()) {
            throw ExceptionFactory.invalidParam(field, MUST_NOT_BE_EMPTY);
        }
    }

    public static void notEmpty(Map<?, ?> map, String field) {
        notNull(map, field);
        if (map.isEmpty()) {
            throw ExceptionFactory.invalidParam(field, MUST_NOT_BE_EMPTY);
        }
    }

    public static void contains(Object obj, Collection<?> collection, String field) {
        notNull(obj, field);

        if (!collection.contains(obj)) {
            throw ExceptionFactory.invalidParam(field, String.format(MUST_BE_ONE_OF, collection));
        }
    }

    public static <T> void containsKey(T key, Map<T, ?> map, String valueField) {
        containsKey(key, map, KEY, valueField);
    }

    public static <T> void containsKey(T key, Map<T, ?> map, String keyField, String valueField) {
        notNull(key, keyField);
        notNull(map, valueField);

        if (!map.containsKey(key)) {
            throw ExceptionFactory.invalidParam(valueField, INVALID_VALUE);
        }
    }

    public static void notAllNull(List<String> fields, Object... values) {
        if (Arrays.stream(values).allMatch(Objects::isNull)) {
            throw ExceptionFactory.invalidParam(String.join(COMMA_SEPARATOR, fields), ALL_VALUES_ARE_NULL);
        }
    }

    public static <T> T parse(Object value, Function<Object, T> parser, String field) {
        notNull(value, field);
        try {
            return parser.apply(value);
        } catch (Exception e) {
            throw ExceptionFactory.invalidParam(field, FAILED_TO_PARSE, e);
        }
    }

    public static void equals(Object value, Object expected, String field) {
        if (!Objects.equals(value, expected)) {
            throw ExceptionFactory.invalidParam(field, String.format(MUST_BE, expected));
        }
    }

    public static void notZero(Integer value, String field) {
        notNull(value, field);

        if (value == 0) {
            throw ExceptionFactory.invalidParam(field, MUST_NOT_BE_ZERO);
        }
    }

    public static void valid(Supplier<Boolean> test, String field, String message) {
        if (!test.get()) {
            throw ExceptionFactory.invalidParam(field, message);
        }
    }

    public static void doesNotContainNull(Map<?, ?> value, String field) {
        notNull(value, field);

        value.forEach((key, v) -> notNull(v, String.format(FIELD_PATH, field, key)));
    }

    public static void doesNotContainNull(Collection<?> value, String field) {
        notNull(value, field);

        if (value.stream().anyMatch(Objects::isNull)) {
            throw ExceptionFactory.invalidParam(field, MUST_NOT_CONTAIN_NULL_VALUES);
        }
    }

    public static <T> void containsAll(Collection<T> values, Supplier<Collection<T>> allowedItemsSupplier, String field) {
        doesNotContainNull(values, field);

        Collection<T> allowedItems = allowedItemsSupplier.get();
        List<T> missing = values.stream()
            .filter(value -> !allowedItems.contains(value))
            .toList();

        if (!missing.isEmpty()) {
            throw ExceptionFactory.invalidParam(field, String.format(UNSUPPORTED_VALUES, missing));
        }
    }
}
