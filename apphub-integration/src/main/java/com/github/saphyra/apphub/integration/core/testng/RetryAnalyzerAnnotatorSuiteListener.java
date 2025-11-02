package com.github.saphyra.apphub.integration.core.testng;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class RetryAnalyzerAnnotatorSuiteListener implements ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        suite.getAllMethods()
            .forEach(testNGMethod -> testNGMethod.setRetryAnalyzerClass(RetryAnalyzer.class));
    }
}
