package com.github.saphyra.apphub.integration.action.frontend.task_manager;

import com.github.saphyra.apphub.integration.structure.view.task_manager.Invitation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TaskManagerIndexPageActions {
    public static void createOrganization(WebDriver driver) {
        driver.findElement(By.id("task-manager-index-create-organization-button"))
            .click();
    }

    public static List<WebElement> getOrganizations(WebDriver driver) {
        return driver.findElements(By.className("task-manager-index-organization"));
    }

    public static List<Invitation> getInvitations(WebDriver driver) {
        return driver.findElements(By.className("task-manager-index-invitation"))
            .stream()
            .map(Invitation::new)
            .toList();
    }
}
