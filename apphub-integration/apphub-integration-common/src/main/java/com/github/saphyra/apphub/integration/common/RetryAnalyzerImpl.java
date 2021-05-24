package com.github.saphyra.apphub.integration.common;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Slf4j
public class RetryAnalyzerImpl implements IRetryAnalyzer {
    private static final int MAX_RETRY_COUNT = 3;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("Retrying test for {} time", retryCount);
            return true;
        }
        return false;
    }
}
