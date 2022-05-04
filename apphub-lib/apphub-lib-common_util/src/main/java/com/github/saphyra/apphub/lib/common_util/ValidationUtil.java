package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;

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

    public static void maximum(Integer value, Integer max, String field) {
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
}
