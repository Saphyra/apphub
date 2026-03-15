package com.github.saphyra.apphub.integration.core.feature_lock;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

public class FeatureLockListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        getAnnotation(method)
            .ifPresent(FeatureLockListener::acquirePermits);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        getAnnotation(method)
            .ifPresent(FeatureLockListener::releasePermits);
    }

    private static void releasePermits(FeatureLocked featureLocked) {
        List<Feature> features = sortFeatures(featureLocked.value())
            .sorted(Comparator.reverseOrder())
            .toList();

        for (Feature feature : features) {
            Semaphore semaphore = FeatureLockManager.getLock(feature);
            semaphore.release();
        }
    }

    private static void acquirePermits(FeatureLocked featureLocked) {
        List<Feature> features = sortFeatures(featureLocked.value())
            .toList();

        for (Feature feature : features) {
            Semaphore semaphore = FeatureLockManager.getLock(feature);
            semaphore.acquireUninterruptibly();
        }
    }

    private static Stream<Feature> sortFeatures(Feature[] features) {
        return Arrays.stream(features)
            .sorted(Comparator.comparing(Enum::name))
            .distinct();
    }

    private Optional<FeatureLocked> getAnnotation(IInvokedMethod method) {
        Method javaMethod = method.getTestMethod()
            .getConstructorOrMethod()
            .getMethod();

        return Optional.ofNullable(javaMethod.getAnnotation(FeatureLocked.class));
    }
}
