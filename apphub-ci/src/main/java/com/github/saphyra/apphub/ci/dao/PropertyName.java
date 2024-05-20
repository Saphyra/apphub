package com.github.saphyra.apphub.ci.dao;

public enum PropertyName {
    //Platform
    LANGUAGE,

    //Local run
    LOCAL_DEPLOY_MODE,
    BUILD_THREAD_COUNT_DEFAULT,
    BUILD_THREAD_COUNT_SKIP_TESTS,
    LOCAL_RUN_INTEGRATION_TESTS_THREAD_COUNT,

    //Remote
    REMOTE_DEPLOY_MODE,
    REMOTE_INTEGRATION_TESTS_THREAD_COUNT,
}
