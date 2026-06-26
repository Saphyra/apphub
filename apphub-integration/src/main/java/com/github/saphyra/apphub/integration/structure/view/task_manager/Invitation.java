package com.github.saphyra.apphub.integration.structure.view.task_manager;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Invitation {
    private final WebElement webElement;

    public void reject(WebDriver driver) {
        webElement.findElement(By.className("task-manager-index-invitation-reject-button"))
            .click();

        driver.findElement(By.id("task-manager-index-invitation-reject-confirmation-dialog-confirm-button"))
            .click();
    }

    public void accept(WebDriver driver) {
        webElement.findElement(By.className("task-manager-index-invitation-accept-button"))
            .click();

        driver.findElement(By.id("task-manager-index-invitation-accept-confirmation-dialog-confirm-button"))
            .click();
    }
}
