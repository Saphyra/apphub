package com.github.saphyra.apphub.integration.common;

import lombok.extern.slf4j.Slf4j;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SkipDisabledTestsInterceptor implements IMethodInterceptor {
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        List<IMethodInstance> testsToRun = methods.stream()
            .filter(this::shouldRun)
            .collect(Collectors.toList());
        Collections.shuffle(testsToRun);
        return testsToRun;
    }

    public boolean shouldRun(IMethodInstance method) {
        List<String> groups = Arrays.asList(method.getMethod().getGroups());

        return TestBase.DISABLED_TEST_GROUPS.stream()
            .noneMatch(groups::contains);
    }
}
