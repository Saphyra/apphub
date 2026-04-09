package com.github.saphyra.apphub.service.feature.elite_base.dao;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class OrphanedRecordCleaner {
    protected final ErrorReporterService errorReporterService;
    protected final MonitoringInstruments monitoringInstruments;

    public abstract Orphanage getOrphanage();

    public abstract List<Orphanage> getPreconditions();

    public synchronized int cleanupOrphanedRecords() {
        try {
            return monitoringInstruments.wrap(
                this::doCleanup,
                Feature.ELITE_BASE_ORPHANED_RECORD_CLEANUP,
                getClass().getSimpleName()
            );
        } catch (Exception e) {
            errorReporterService.report("Exception occurred while running " + getClass().getSimpleName(), e);
            return 0;
        }
    }

    protected abstract int doCleanup();
}
