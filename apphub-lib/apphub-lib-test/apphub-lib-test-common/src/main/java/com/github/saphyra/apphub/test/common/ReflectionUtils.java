package com.github.saphyra.apphub.test.common;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

import static java.util.Objects.nonNull;

@UtilityClass
public class ReflectionUtils {
    @SuppressWarnings("unchecked")
    public <T> T getFieldValue(Object object, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Class<?> current = object.getClass();

        while (nonNull(current)) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (T) field.get(object);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Field " + fieldName + " not found in class or superclass");
    }
}
