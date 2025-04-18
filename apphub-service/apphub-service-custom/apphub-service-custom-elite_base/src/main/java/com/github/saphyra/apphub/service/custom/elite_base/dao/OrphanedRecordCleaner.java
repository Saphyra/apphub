package com.github.saphyra.apphub.service.custom.elite_base.dao;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public abstract class OrphanedRecordCleaner {
    protected final ErrorReporterService errorReporterService;

    public synchronized int cleanupOrphanedRecords() {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            int rowsDeleted = doCleanup();
            stopwatch.stop();
            log.info("{} finished in {} ms. {} rows were deleted.", getClass().getSimpleName(), stopwatch.elapsed(TimeUnit.MILLISECONDS), rowsDeleted);

            return rowsDeleted;
        } catch (Exception e) {
            log.error("Exception occurred while running {}", getClass().getSimpleName(), e);
            errorReporterService.report("Exception occurred while running " + getClass().getSimpleName(), e);
            return  0;
        }
    }

    protected abstract int doCleanup();
}
