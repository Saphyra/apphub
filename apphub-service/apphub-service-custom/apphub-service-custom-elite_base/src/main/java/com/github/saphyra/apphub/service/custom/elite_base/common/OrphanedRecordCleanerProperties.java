package com.github.saphyra.apphub.service.custom.elite_base.common;

import lombok.Data;

import java.time.Duration;

@Data
public class OrphanedRecordCleanerProperties {
    private Integer processorParallelism;
    private Duration timeout;
    private Integer batchSize;
}
