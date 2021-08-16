package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;

import java.util.concurrent.Executors;

public class ExecutorServiceBeenTestUtils {
    private static final SleepService SLEEP_SERVICE = new SleepService();

    public static ExecutorServiceBean create(ErrorReporterService errorReporterService) {
        return createFactory(errorReporterService).create(Executors.newSingleThreadExecutor());
    }

    public static ExecutorServiceBeanFactory createFactory(ErrorReporterService errorReporterService) {
        return new ExecutorServiceBeanFactory(SLEEP_SERVICE, errorReporterService);
    }
}
