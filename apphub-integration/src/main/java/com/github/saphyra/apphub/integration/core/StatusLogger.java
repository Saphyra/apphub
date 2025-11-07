package com.github.saphyra.apphub.integration.core;

import lombok.extern.slf4j.Slf4j;
import org.testng.ISuite;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StatusLogger {
    private static int TOTAL_TEST_COUNT;
    private static final Set<String> FINISHED_TESTS = ConcurrentHashMap.newKeySet();
    private static final List<String> TEST_START_ORDER = new Vector<>();

    public static void setTotalTestCount(ISuite suite) {
        TOTAL_TEST_COUNT = (int) suite
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
        FINISHED_TESTS.remove(TestUtils.getMethodIdentifier(method));
    }

    public static void addToStartOrder(Method method) {
        TEST_START_ORDER.add(TestUtils.getTestCaseName(method));
    }

    public static synchronized void incrementFinishedTestCount(Method method, long duration) {
        String methodIdentifier = TestUtils.getMethodIdentifier(method);
        FINISHED_TESTS.add(methodIdentifier);

        int finishedPercentage = (int) Math.floor((double) FINISHED_TESTS.size() / TOTAL_TEST_COUNT * 100);

        StringBuilder progressBar = new StringBuilder();

        progressBar.append("|");
        for (int i = 0; i < 100; i += 2) {
            if (i < finishedPercentage) {
                progressBar.append("=");
            } else {
                progressBar.append(".");
            }
        }
        progressBar.append("|");

        log.info("{} {}% {}/{} - {} Duration: {}ms", progressBar, finishedPercentage, FINISHED_TESTS.size(), TOTAL_TEST_COUNT, TestUtils.getTestCaseName(method), duration);
    }
}
