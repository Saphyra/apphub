package com.github.saphyra.apphub.service.admin_panel.migration_task.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MigrationTask {
    private final String event;
    private final String name;
    private Boolean completed;
}
