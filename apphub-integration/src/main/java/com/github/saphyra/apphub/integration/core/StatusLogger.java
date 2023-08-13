package com.github.saphyra.apphub.integration.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StatusLogger {
    private static int TOTAL_TEST_COUNT;
    private static final Set<String> FINISHED_TESTS = ConcurrentHashMap.newKeySet();
    private static final List<String> TEST_START_ORDER = new Vector<>();

    public static void setTotalTestCount(ITestContext context) {
        TOTAL_TEST_COUNT = (int) context.getSuite()
            .getAllMethods()
            .stream()
            .filter(TestBase::isEnabled)
            .count();
        log.info("Total test count: {}", TOTAL_TEST_COUNT);
    }

    public static void logTestStartOrder() {
        for (int i = 0; i < TEST_START_ORDER.size(); i++) {
            log.info("{} - {}", i, TEST_START_ORDER.get(i));
        }
    }

    public static void removeFinishedTest(Method method) {
        FINISHED_TESTS.remove(getMethodIdentifier(method));
    }

    public static void addToStartOrder(Method method) {
        TEST_START_ORDER.add(getTestCaseName(method));
    }

    public static synchronized void incrementFinishedTestCount(Method method) {
        String methodIdentifier = getMethodIdentifier(method);
        FINISHED_TESTS.add(methodIdentifier);

        int finishedPercentage = (int) Math.floor((double) FINISHED_TESTS.size() / TOTAL_TEST_COUNT * 100);

        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < 100; i += 2) {
            if (i < finishedPercentage) {
                progressBar.append("=");
            } else {
                progressBar.append("_");
            }
        }

        log.info("{} {}% {}/{} - {}", progressBar, finishedPercentage, FINISHED_TESTS.size(), TOTAL_TEST_COUNT, getTestCaseName(method));
    }

    private static String getMethodIdentifier(Method method) {
        return method.getDeclaringClass().getName() + method.getName();
    }

    private static String getTestCaseName(Method method) {
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
