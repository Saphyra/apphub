package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;

@ForRemoval("notebook-redesign")
public interface NotebookMigrationController {
    @PostMapping(Endpoints.NOTEBOOK_EVENT_MIGRATION_CHECKLIST)
    void migrateChecklists();

    @PostMapping(Endpoints.NOTEBOOK_EVENT_MIGRATION_TABLE)
    void migrateTables();
}
