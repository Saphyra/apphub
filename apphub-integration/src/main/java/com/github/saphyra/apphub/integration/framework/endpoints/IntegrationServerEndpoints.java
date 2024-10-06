package com.github.saphyra.apphub.integration.framework.endpoints;

public class IntegrationServerEndpoints {
    public static final String INTEGRATION_SERVER_CREATE_TEST_RUN = "/test-run";
    public static final String INTEGRATION_SERVER_REPORT_TEST_CASE = "/test-case/{testRunId}";
    public static final String INTEGRATION_SERVER_GET_AVERAGE_RUN_TIME = "/test-case/run-time/average";
    public static final String INTEGRATION_SERVER_FINISH_TEST_RUN = "/test-run/{testRunId}";
}
