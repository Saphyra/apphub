package com.github.saphyra.apphub.integration.action.frontend.notebook.view;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ViewTableActions {
    public static void enableEditing(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-edit-button"))
            .click();
    }

    public static void setTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(getTitleInput(driver), title);
    }

    private static WebElement getTitleInput(WebDriver driver) {
        return driver.findElement(By.id("notebook-content-list-item-title-input"));
    }

    public static void saveChanges(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-save-button"))
            .click();
    }

    public static List<TableHead> getTableHeads(WebDriver driver) {
        return driver.findElements(By.cssSelector(".table-head"))
            .stream()
            .map(TableHead::new)
            .collect(Collectors.toList());
    }

    public static void discardChanges(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-discard-button"))
            .click();

        driver.findElement(By.id("notebook-content-table-discard-confirm-button"))
            .click();
    }

    public static boolean isEditingEnabled(WebDriver driver) {
        return WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-table-edit-button")))
            .isEmpty();
    }

    public static String getTitle(WebDriver driver) {
        return getTitleInput(driver)
            .getAttribute("value");
    }

    public static void addRowToStart(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-add-row-to-start"))
            .click();
    }

    public static void addRowToEnd(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-add-row-to-end"))
            .click();
    }

    public static List<TableRow> getRows(WebDriver driver) {
        return driver.findElements(By.cssSelector(".notebook-table-row"))
            .stream()
            .map(TableRow::new)
            .collect(Collectors.toList());
    }

    public static void newColumn(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-new-column-button"))
            .click();
    }

    public static void deleteChecked(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-delete-checked-button"))
            .click();

        driver.findElement(By.id("notebook-content-table-delete-checked-confirm-button"))
            .click();
    }

    public static void close(WebDriver driver) {
        driver.findElement(By.id("notebook-content-list-item-close-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-table"))).isEmpty())
            .assertTrue("Table is not closed.");
    }

    public static void convertTableToChecklistTable(WebDriver driver) {
        driver.findElement(By.id("notebook-content-table-convert-button"))
            .click();

        driver.findElement(By.id("notebook-content-table-conversion-confirm-button"))
            .click();
    }

    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-content-list-item-title-input")), title);
    }
}
