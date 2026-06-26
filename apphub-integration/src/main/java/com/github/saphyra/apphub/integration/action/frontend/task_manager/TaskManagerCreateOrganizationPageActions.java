package com.github.saphyra.apphub.integration.action.frontend.task_manager;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TaskManagerCreateOrganizationPageActions {
    public static void fillName(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("task-manager-create-organization-name")), title);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("task-manager-create-organization-create-button"))
            .click();
    }

    public static void searchAndInvite(WebDriver driver, String credential) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("task-manager-create-organization-user-search-input")), credential);
        driver.findElement(By.id("task-manager-create-organization-user-search-button"))
            .click();

        WebElement foundUser = AwaitilityWrapper.getListWithWait(() -> driver.findElements(By.cssSelector("#task-manager-create-organization-user-search-results .task-manager-create-organization-user")), webElements -> !webElements.isEmpty())
            .getFirst();
        foundUser.findElement(By.className("task-manager-create-organization-user-invite-button"))
            .click();
    }
}
