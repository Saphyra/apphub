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
    public static void notNull(Object value, String fieldName) {
        if (isNull(value)) {
            throw ExceptionFactory.invalidParam(fieldName, "must not be null");
        }
    }

    public static void notBlank(String value, String fieldName) {
        if (isBlank(value)) {
            throw ExceptionFactory.invalidParam(fieldName, "must not be null or blank");
        }
    }

    public static <T, R> R convertToEnumChecked(T value, Function<T, R> mapper, String fieldName) {
        try {
            notNull(value, fieldName);
            return mapper.apply(value);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.invalidParam(fieldName, "invalid value");
        }
    }

    public static void length(String value, int length, String field) {
        notNull(value, field);
        if (value.length() != length) {
            throw ExceptionFactory.invalidParam(field, "must be " + length + " character(s) long");
        }
    }

    public static void minLength(String value, int minLength, String field) {
        notNull(value, field);
        if (value.length() < minLength) {
            throw ExceptionFactory.invalidParam(field, "too short");
        }
    }

    public static void maxLength(String value, int maxLength, String field) {
        notNull(value, field);
        if (value.length() > maxLength) {
            throw ExceptionFactory.invalidParam(field, "too long");
        }
    }

    public static void atLeastExclusive(Double value, double minValue, String field) {
        notNull(value, field);
        if (value <= minValue) {
            throw ExceptionFactory.invalidParam(field, "too low");
        }
    }

    public static void atLeast(Double value, double minValue, String field) {
        notNull(value, field);
        if (value < minValue) {
            throw ExceptionFactory.invalidParam(field, "too low");
        }
    }

    public static void atLeast(Integer value, int minValue, String field) {
        notNull(value, field);
        if (value < minValue) {
            throw ExceptionFactory.invalidParam(field, "too low");
        }
    }

    public static void atLeast(Long value, int minValue, String field) {
        notNull(value, field);
        if (value < minValue) {
            throw ExceptionFactory.invalidParam(field, "too low");
        }
    }

    public static void maximum(Integer value, Integer max, String field) {
        notNull(value, field);
        if (value > max) {
            throw ExceptionFactory.invalidParam(field, "too high");
        }
    }

    public static void maximum(Double value, double max, String field) {
        notNull(value, field);
        if (value > max) {
            throw ExceptionFactory.invalidParam(field, "too high");
        }
    }

    public static void maximum(Long value, Long max, String field) {
        notNull(value, field);
        if (value > max) {
            throw ExceptionFactory.invalidParam(field, "too high");
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
            throw ExceptionFactory.invalidParam(field, "must not be empty");
        }
    }

    public static void notEmpty(Map<?, ?> map, String field) {
        notNull(map, field);
        if (map.isEmpty()) {
            throw ExceptionFactory.invalidParam(field, "must not be empty");
        }
    }

    public static void contains(Object obj, Collection<?> collection, String field) {
        notNull(obj, field);

        if (!collection.contains(obj)) {
            throw ExceptionFactory.invalidParam(field, "must be one of " + collection);
        }
    }

    public static <T> void containsKey(T key, Map<T, ?> map, String valueField) {
        containsKey(key, map, "key", valueField);
    }

    public static <T> void containsKey(T key, Map<T, ?> map, String keyField, String valueField) {
        notNull(key, keyField);
        notNull(map, valueField);

        if (!map.containsKey(key)) {
            throw ExceptionFactory.invalidParam(valueField, "invalid value");
        }
    }

    public static void notAllNull(List<String> fields, Object... values) {
        if (Arrays.stream(values).allMatch(Objects::isNull)) {
            throw ExceptionFactory.invalidParam(String.join(", ", fields), "all values are null");
        }
    }

    public static <T> T parse(Object value, Function<Object, T> parser, String field) {
        notNull(value, field);
        try {
            return parser.apply(value);
        } catch (Exception e) {
            throw ExceptionFactory.invalidParam(field, "failed to parse", e);
        }
    }

    public static void equals(Object value, Object expected, String field) {
        if (!Objects.equals(value, expected)) {
            throw ExceptionFactory.invalidParam(field, "must be " + expected);
        }
    }

    public static void notZero(Integer value, String field) {
        notNull(value, field);

        if (value == 0) {
            throw ExceptionFactory.invalidParam(field, "must not be zero");
        }
    }

    public static void valid(Supplier<Boolean> test, String field, String message) {
        if (!test.get()) {
            throw ExceptionFactory.invalidParam(field, message);
        }
    }

    public static void doesNotContainNull(Map<?, ?> value, String field) {
        notNull(value, field);

        value.forEach((key, v) -> notNull(v, "%s.%s".formatted(field, key)));
    }
}
