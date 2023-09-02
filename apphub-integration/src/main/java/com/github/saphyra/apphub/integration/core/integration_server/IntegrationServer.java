package com.github.saphyra.apphub.integration.core.integration_server;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.core.TestUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class IntegrationServer {
    private static volatile UUID TEST_RUN_ID;

    public static void start() {
        if (TestConfiguration.INTEGRATION_SERVER_ENABLED) {
            TEST_RUN_ID = IntegrationServerApi.createTestRun();
        }
    }

    public static void stop(String status) {
        if (TestConfiguration.INTEGRATION_SERVER_ENABLED) {
            IntegrationServerApi.completeTestRun(TEST_RUN_ID, status);
        }
    }

    public static void reportTestCaseRun(Method method, long duration, boolean passed) {
        if (TestConfiguration.INTEGRATION_SERVER_ENABLED) {
            String testCaseName = TestUtils.getTestCaseName(method);
            String methodIdentifier = TestUtils.getMethodIdentifier(method);
            List<String> groups = Arrays.asList(method.getAnnotation(Test.class).groups());

            IntegrationServerApi.reportTestCaseRun(TEST_RUN_ID, methodIdentifier, testCaseName, groups, duration, passed ? Constants.PASSED : Constants.FAILED);
        }
    }

    public static long getAverageDuration(Method method) {
        String methodIdentifier = TestUtils.getMethodIdentifier(method);
        return getAverageDuration(methodIdentifier);
    }

    public static long getAverageDuration(String methodIdentifier) {
        return IntegrationServerApi.getAverageDuration(methodIdentifier);
    }
}
