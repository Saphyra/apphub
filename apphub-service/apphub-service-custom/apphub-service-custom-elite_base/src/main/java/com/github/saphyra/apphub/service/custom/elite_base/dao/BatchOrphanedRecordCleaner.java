package com.github.saphyra.apphub.service.custom.elite_base.dao;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class BatchOrphanedRecordCleaner extends OrphanedRecordCleaner {
    protected final EliteBaseProperties eliteBaseProperties;

    public BatchOrphanedRecordCleaner(ErrorReporterService errorReporterService, EliteBaseProperties eliteBaseProperties) {
        super(errorReporterService);
        this.eliteBaseProperties = eliteBaseProperties;
    }

    @Override
    protected int doCleanup() {
        List<String> idsToDelete;
        int totalDeleted = 0;
        do {
            idsToDelete = fetchIds();
            if (!idsToDelete.isEmpty()) {
                log.info("Deleting {} records by {}", idsToDelete.size(), getClass().getSimpleName());
                delete(idsToDelete);
            }
            totalDeleted += idsToDelete.size();
        } while (idsToDelete.size() >= eliteBaseProperties.getOrphanedRecordCleaner().getBatchSize());

        return totalDeleted;
    }

    protected abstract List<String> fetchIds();

    protected abstract void delete(List<String> idsToDelete);
}
