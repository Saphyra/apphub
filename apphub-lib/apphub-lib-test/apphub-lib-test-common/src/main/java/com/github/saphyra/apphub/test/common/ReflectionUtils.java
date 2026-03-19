package com.github.saphyra.apphub.test.common;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtils {
    @SuppressWarnings("unchecked")
    public <T> T getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass()
            .getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(object);
    }
}
