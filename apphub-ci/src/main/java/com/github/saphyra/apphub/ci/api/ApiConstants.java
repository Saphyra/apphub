package com.github.saphyra.apphub.ci.api;

public class ApiConstants {
    public static final String REDIRECT = "redirect:";

    public static final String PATH_VARIABLE_ENVIRONMENT = "{environment}";

    public static final String PATH_ENV_OPS = "/v2/env-ops/" + PATH_VARIABLE_ENVIRONMENT;
    public static final String PATH_ENV_OPS_VM = PATH_ENV_OPS + "/vm";

    public static final String PARAM_ENVIRONMENT = "environment";
    public static final String PARAM_TASK_QUEUE_SIZE = "task_queue_size";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_SUCCESS = "success";
    public static final String PARAM_START_USER_DEFINED_SERVICES = "start_user_defined_services";
    public static final String PARAM_USER_DEFINED_SERVICES = "user_defined_services";
    public static final String PARAM_BUILD_THREAD_COUNT = "build_thread_count";
    public static final String PARAM_STARTUP_COUNT_LIMIT = "startup_count_limit";
    public static final String PARAM_SKIP_TESTS = "skip_tests";
    public static final String PARAM_DISABLED_SERVICES = "disabled_services";
    public static final String PARAM_SERVICES = "services";
    public static final String PARAM_FILTER_TESTS = "filter_tests";
    public static final String PARAM_TEST_FILTER = "test_filter";
    public static final String PARAM_TEST_THREAD_COUNT = "test_thread_count";
    public static final String PARAM_PRE_CREATE_DRIVER_COUNT = "pre_create_driver_count";
    public static final String PARAM_RETRY_COUNT = "retry_count";
    public static final String PARAM_STOP_USER_DEFINED_SERVICES = "stop_user_defined_services";

    public static final String PARAM_PREFIX_ENABLED_SERVICE = "enabled_service_";
}
