package com.github.saphyra.apphub.service.notebook.migration.checklist;

import com.github.saphyra.apphub.service.notebook.migration.MigrationContext;
import com.github.saphyra.apphub.service.notebook.migration.StaticApplicationContext;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChecklistMigrationTask implements CustomTaskChange {
    @Override
    public void execute(Database database) throws CustomChangeException {
        log.info("Migrating Checklists...");
        MigrationContext context = StaticApplicationContext.getApplicationContextProxy()
            .getBean(MigrationContext.class);
        context.getChecklistMigrationService()
            .migrate();
    }

    @Override
    public String getConfirmationMessage() {
        return "Checklists successfully migrated.";
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
