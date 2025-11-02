package com.github.saphyra.apphub.integration.core.testng;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static com.github.saphyra.apphub.integration.core.TestConfiguration.RETRY_ENABLED;

@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int MAX_RETRY_COUNT = 2;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (RETRY_ENABLED && retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("Retrying test {} for {} time", iTestResult.getName(), retryCount, iTestResult.getThrowable());
            return true;
        }
        return false;
    }
}
