package com.github.saphyra.apphub.service.notebook.migration;

import com.github.saphyra.apphub.service.notebook.migration.checklist.ChecklistMigrationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class MigrationContext {
    private final ChecklistMigrationService checklistMigrationService;
}
