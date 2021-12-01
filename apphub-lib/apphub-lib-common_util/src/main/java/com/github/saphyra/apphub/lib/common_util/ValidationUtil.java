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

    public static <T, R> void enumElementExists(T value, Function<T, R> mapper, String fieldName) {
        try {
            notNull(value, fieldName);
            mapper.apply(value);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.invalidParam(fieldName, "invalid value");
        }
    }
}
