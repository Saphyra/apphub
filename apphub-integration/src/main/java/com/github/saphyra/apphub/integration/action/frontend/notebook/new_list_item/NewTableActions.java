package com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class NewTableActions {
    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-table-title")), title);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-create-button"))
            .click();
    }

    public static void newRow(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-new-row-button"))
            .click();
    }

    public static List<TableRow> getRows(WebDriver driver) {
        return driver.findElements(By.cssSelector(".notebook-table-row"))
            .stream()
            .map(TableRow::new)
            .collect(Collectors.toList());
    }

    public static void newColumn(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-new-column-button"))
            .click();
    }

    public static List<TableHead> getTableHeads(WebDriver driver) {
        return driver.findElements(By.cssSelector(".table-head"))
            .stream()
            .map(TableHead::new)
            .collect(Collectors.toList());
    }
}
