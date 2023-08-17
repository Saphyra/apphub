package com.github.saphyra.apphub.integration.core;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TestUtils {
    public static String getMethodIdentifier(Method method) {
        return String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
    }

    public static String getTestCaseName(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        return String.format("%s %s.%s", TestType.getLabel(declaringClass.getSuperclass()), declaringClass.getSimpleName(), method.getName());
    }

    @RequiredArgsConstructor
    private enum TestType {
        BE(BackEndTest.class, "[BE]"),
        FE(SeleniumTest.class, "[FE]");

        private final Class<? extends TestBase> clazz;
        private final String label;

        public static String getLabel(Class<?> declaringClass) {
            return Arrays.stream(values())
                .filter(testType -> testType.clazz.equals(declaringClass))
                .findFirst()
                .map(testType -> testType.label)
                .orElseThrow(() -> new RuntimeException("No TestType present for " + declaringClass.getName()));
        }
    }
}
