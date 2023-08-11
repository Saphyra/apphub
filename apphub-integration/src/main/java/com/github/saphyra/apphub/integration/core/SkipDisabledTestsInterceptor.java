package com.github.saphyra.apphub.integration.core;

import lombok.extern.slf4j.Slf4j;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.core.TestBase.isEnabled;

@Slf4j
public class SkipDisabledTestsInterceptor implements IMethodInterceptor {
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        return methods.stream()
            .filter(iMethodInstance -> isEnabled(iMethodInstance.getMethod()))
            .sorted(Comparator.comparingInt(method -> method.getMethod().getPriority()))
            .collect(Collectors.toList());
    }
}
