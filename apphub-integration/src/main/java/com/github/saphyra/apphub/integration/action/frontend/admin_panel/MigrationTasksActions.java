package com.github.saphyra.apphub.integration.action.frontend.admin_panel;

import com.github.saphyra.apphub.integration.structure.view.admin_panel.MigrationTask;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class MigrationTasksActions {
    public static MigrationTask findMigrationTaskByEventValidated(WebDriver driver, String event) {
        return findMigrationTaskByEvent(driver, event)
            .orElseThrow(() -> new RuntimeException("MigrationTask not found by event " + event));
    }

    public static Optional<MigrationTask> findMigrationTaskByEvent(WebDriver driver, String event) {
        return driver.findElements(By.className("migration-task"))
            .stream()
            .map(MigrationTask::new)
            .filter(migrationTask -> migrationTask.getEvent().equals(event))
            .findAny();
    }
}
