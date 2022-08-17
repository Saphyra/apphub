package com.github.saphyra.apphub.integration.core;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Slf4j
public class RetryAnalyzerImpl implements IRetryAnalyzer {
    private static final int MAX_RETRY_COUNT = 4;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("Retrying test {} for {} time", iTestResult.getName(), retryCount, iTestResult.getThrowable());
            return true;
        }
        return false;
    }
}
