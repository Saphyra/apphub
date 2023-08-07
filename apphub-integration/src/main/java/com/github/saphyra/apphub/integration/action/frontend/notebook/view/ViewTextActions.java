package com.github.saphyra.apphub.integration.action.frontend.notebook.view;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ViewTextActions {
    public static String getContent(WebDriver driver) {
        return getContentInput(driver)
            .getAttribute("value");
    }

    private static WebElement getContentInput(WebDriver driver) {
        return driver.findElement(By.id("notebook-content-text-content"));
    }

    public static String getTitle(WebDriver driver) {
        return getTitleInput(driver)
            .getAttribute("value");
    }

    private static WebElement getTitleInput(WebDriver driver) {
        return driver.findElement(By.cssSelector(".notebook-list-item-title > input"));
    }

    public static void enableEditing(WebDriver driver) {
        driver.findElement(By.id("notebook-content-text-edit-button"))
            .click();
    }

    public static void setTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(getTitleInput(driver), title);
    }

    public static void saveChanges(WebDriver driver) {
        driver.findElement(By.id("notebook-content-text-save-button"))
            .click();
    }

    public static void setContent(WebDriver driver, String content) {
        WebElementUtils.clearAndFill(getContentInput(driver), content);
    }

    public static void discardChanges(WebDriver driver) {
        driver.findElement(By.id("notebook-content-text-discard-button"))
            .click();

        driver.findElement(By.id("notebook-content-checklist-discard-confirm-button"))
            .click();
    }

    public static void close(WebDriver driver) {
        driver.findElement(By.id("notebook-content-text-close-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-text"))).isEmpty())
            .assertTrue("Text is not closed.");
    }
}
