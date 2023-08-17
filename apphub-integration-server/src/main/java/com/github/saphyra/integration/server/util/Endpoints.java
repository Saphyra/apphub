package com.github.saphyra.integration.server.util;

public class Endpoints {
    public static final String CREATE_TEST_RUN = "/test-run";
    public static final String REPORT_TEST_CASE = "/test-case/{testRunId}";
    public static final String GET_AVERAGE_RUN_TIME = "/test-case/run-time/average";
    public static final String FINISH_TEST_RUN = "/test-run/{testRunId}";
    public static final String HEALTH = "/health";
}
