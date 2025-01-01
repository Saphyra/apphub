package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;

import java.util.concurrent.Executors;

public class FixedExecutorServiceBean extends ExecutorServiceBean {
    public FixedExecutorServiceBean(int threadCount, ErrorReporterService errorReporterService) {
        super(Executors.newFixedThreadPool(threadCount), errorReporterService);
    }
}
