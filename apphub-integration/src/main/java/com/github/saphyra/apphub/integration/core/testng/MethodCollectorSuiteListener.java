package com.github.saphyra.apphub.integration.core.testng;

import com.github.saphyra.apphub.integration.core.StatusLogger;
import com.github.saphyra.apphub.integration.core.integration_server.IntegrationServer;
import com.github.saphyra.apphub.integration.framework.Constants;
import lombok.extern.slf4j.Slf4j;
import org.testng.ISuite;
import org.testng.ISuiteListener;

@Slf4j
public class MethodCollectorSuiteListener implements ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        StatusLogger.setTotalTestCount(suite);
    }

    @Override
    public void onFinish(ISuite suite) {
        boolean anyFailure = suite.getResults()
            .values()
            .stream()
            .anyMatch(iSuiteResult -> iSuiteResult.getTestContext().getFailedTests().size() > 0);
        IntegrationServer.stop(anyFailure ? Constants.FAILED : Constants.PASSED);
    }
}
