package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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

    public static void notEmpty(Collection<?> collection, String field) {
        notNull(collection, field);
        if (collection.isEmpty()) {
            throw ExceptionFactory.invalidParam(field, "must not be empty");
        }
    }

    public static void contains(Object obj, List<Object> edit, String field) {
        notNull(obj, field);

        if (!edit.contains(obj)) {
            throw ExceptionFactory.invalidParam(field, "must be one of " + edit);
        }
    }

    public static void notAllNull(List<String> fields, Object... values) {
        if (Arrays.stream(values).allMatch(Objects::isNull)) {
            throw ExceptionFactory.invalidParam(String.join(", ", fields), "all values are null");
        }
    }
}
