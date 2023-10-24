package com.github.saphyra.apphub.integration.structure.view.admin_panel;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
public class MigrationTask {
    private final WebElement webElement;

    public String getEvent() {
        return webElement.findElement(By.className("migration-task-event"))
            .getText();
    }

    public void trigger(WebDriver driver) {
        webElement.findElement(By.className("migration-task-trigger-button"))
            .click();

        driver.findElement(By.id("migration-tasks-confirm-trigger-button"))
            .click();
    }

    public boolean isCompleted() {
        return webElement.findElement(By.className("migration-task-completed"))
            .isSelected();
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("migration-task-delete-button"))
            .click();

        driver.findElement(By.id("migration-tasks-confirm-deletion-button"))
            .click();
    }
}
