package com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class NewChecklistActions {
    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-checklist-title")), title);
    }

    public static void submitForm(WebDriver driver) {
        driver.findElement(By.id("notebook-new-checklist-create-button"))
            .click();
    }

    public static void fillItem(WebDriver driver, int index, String value) {
        ChecklistItem checklistItem = getItems(driver)
            .get(index);

        checklistItem.setValue(value);
    }

    public static List<ChecklistItem> getItems(WebDriver driver) {
        return driver.findElements(By.cssSelector(".notebook-checklist-item"))
            .stream()
            .map(ChecklistItem::new)
            .collect(Collectors.toList());
    }

    public static void addItem(WebDriver driver) {
        driver.findElement(By.id("notebook-new-checklist-new-item-button"))
            .click();
    }
}
