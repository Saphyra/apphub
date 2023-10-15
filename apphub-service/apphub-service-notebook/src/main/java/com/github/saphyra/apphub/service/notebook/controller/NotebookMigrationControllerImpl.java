package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.server.NotebookMigrationController;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.service.notebook.migration.checklist.ChecklistMigrationService;
import com.github.saphyra.apphub.service.notebook.migration.table.TableMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@ForRemoval("notebook-redesign")
//TODO unit test
public class NotebookMigrationControllerImpl implements NotebookMigrationController {
    private final ChecklistMigrationService checklistMigrationService;
    private final TableMigrationService tableMigrationService;

    @Override
    public void migrateChecklists() {
        checklistMigrationService.migrate();
    }

    @Override
    public void migrateTables() {
        tableMigrationService.migrate();
    }
}
