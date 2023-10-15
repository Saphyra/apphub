package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;

@ForRemoval("notebook-redesign")
public interface NotebookMigrationController {
    @GetMapping(Endpoints.NOTEBOOK_EVENT_MIGRATION_CHECKLIST)
    void migrateChecklists();

    @GetMapping(Endpoints.NOTEBOOK_EVENT_MIGRATION_TABLE)
    void migrateTables();
}
