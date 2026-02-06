package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;

import java.util.concurrent.Executors;

public class ExecutorServiceBeenTestUtils {
    public static ExecutorServiceBean create(ErrorReporterService errorReporterService) {
        return createFactory(errorReporterService).create(Executors.newCachedThreadPool());
    }

    public static ExecutorServiceBeanFactory createFactory(ErrorReporterService errorReporterService) {
        return new ExecutorServiceBeanFactory(errorReporterService);
    }
}
