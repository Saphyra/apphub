package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.service.notebook.migration.checklist.ChecklistMigrationService;
import com.github.saphyra.apphub.service.notebook.migration.table.TableMigrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NotebookMigrationControllerImplTest {
    @Mock
    private ChecklistMigrationService checklistMigrationService;

    @Mock
    private TableMigrationService tableMigrationService;

    @InjectMocks
    private NotebookMigrationControllerImpl underTest;

    @Test
    void migrateChecklists() {
        underTest.migrateChecklists();

        then(checklistMigrationService).should().migrate();
    }

    @Test
    void migrateTables() {
        underTest.migrateTables();

        then(tableMigrationService).should().migrate();
    }
}