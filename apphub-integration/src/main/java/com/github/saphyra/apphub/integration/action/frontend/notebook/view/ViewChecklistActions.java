package com.github.saphyra.apphub.integration.action.frontend.notebook.view;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class ViewChecklistActions {
    public static void enableEditing(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-edit-button"))
            .click();
    }

    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.cssSelector(".notebook-list-item-title > input")), title);
    }

    public static void saveChanges(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-save-button"))
            .click();
    }

    public static List<ChecklistItem> getItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#notebook-content-checklist-content .notebook-checklist-item"))
            .stream()
            .map(ChecklistItem::new)
            .collect(Collectors.toList());
    }

    public static void discardChanges(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-discard-button"))
            .click();

        driver.findElement(By.id("notebook-content-checklist-discard-confirm-button"))
            .click();
    }

    public static void close(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-close-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-checklist"))).isEmpty())
            .assertTrue("Checklist window is not closed.");
    }

    public static void addItem(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-add-item-button"))
            .click();
    }

    public static void fillItem(WebDriver driver, int index, String value) {
        getItems(driver)
            .get(index)
            .setValue(value);
    }

    public static void orderItems(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-order-items-button"))
            .click();

        SleepUtil.sleep(1000);
    }

    public static void deleteChecked(WebDriver driver) {
        driver.findElement(By.id("notebook-content-checklist-delete-checked-button"))
            .click();

        driver.findElement(By.id("notebook-content-checklist-delete-checked-confirm-button"))
            .click();
    }

    public static String getTitle(WebDriver driver) {
        return driver.findElement(By.id("notebook-content-checklist-title"))
            .getAttribute("value");
    }
}
