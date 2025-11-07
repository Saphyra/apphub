package com.github.saphyra.apphub.integration.core.testng;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.core.TestUtils;
import com.github.saphyra.apphub.integration.core.integration_server.IntegrationServer;
import lombok.extern.slf4j.Slf4j;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SkipDisabledTestsInterceptor implements IMethodInterceptor {
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        Map<String, Long> durations = new HashMap<>();
        List<IMethodInstance> methodsToRun = methods.stream()
            .filter(iMethodInstance -> TestBase.isEnabled(iMethodInstance.getMethod()))
            .toList();
        if (TestConfiguration.INTEGRATION_SERVER_ENABLED) {
            durations = methodsToRun.stream()
                .map(iMethodInstance -> iMethodInstance.getMethod().getConstructorOrMethod().getMethod())
                .map(TestUtils::getMethodIdentifier)
                .distinct()
                .collect(Collectors.toMap(Function.identity(), IntegrationServer::getAverageDuration));
        }

        Map<String, Long> d = durations;
        log.info("");
        log.info("Test case order:");
        List<IMethodInstance> result = methodsToRun.stream()
            .sorted((o1, o2) -> compare(o1, o2, d))
            .peek(iMethodInstance -> {
                Method method = iMethodInstance.getMethod().getConstructorOrMethod().getMethod();
                String methodIdentifier = TestUtils.getMethodIdentifier(method);
                String testCaseName = TestUtils.getTestCaseName(method);

                log.info("{}: {}ms", testCaseName, d.get(methodIdentifier));
            })
            .toList();

        log.info("");
        return result;
    }

    private int compare(IMethodInstance o1, IMethodInstance o2, Map<String, Long> durations) {
        if (TestConfiguration.INTEGRATION_SERVER_ENABLED) {
            String mi1 = TestUtils.getMethodIdentifier(o1.getMethod().getConstructorOrMethod().getMethod());
            String mi2 = TestUtils.getMethodIdentifier(o2.getMethod().getConstructorOrMethod().getMethod());

            return Long.compare(
                durations.getOrDefault(mi2, 0L),
                durations.getOrDefault(mi1, 0L)
            );
        }
        return Integer.compare(o1.getMethod().getPriority(), o2.getMethod().getPriority());
    }
}
