package com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NewTableActions {
    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-table-title")), title);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-create-button"))
            .click();
    }

    public static void addRowToEnd(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-add-row-to-end"))
            .click();
    }

    public static List<TableRow> getRows(WebDriver driver) {
        return driver.findElements(By.cssSelector(".notebook-table-row"))
            .stream()
            .map(TableRow::new)
            .collect(Collectors.toList());
    }

    public static void addColumnToEnd(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-add-column-to-end"))
            .click();
    }

    public static List<TableHead> getTableHeads(WebDriver driver) {
        return driver.findElements(By.cssSelector(".table-head"))
            .stream()
            .map(TableHead::new)
            .collect(Collectors.toList());
    }

    public static void setColumnType(WebDriver driver, int rowIndex, int columnIndex, ColumnType columnType) {
        Supplier<TableColumn> columnSupplier = () -> getRows(driver)
            .get(rowIndex)
            .getColumns()
            .get(columnIndex);

        columnSupplier.get()
            .openColumnTypeSelector();

        AwaitilityWrapper.createDefault()
            .until(() -> columnSupplier.get().isColumnTypeSelectorOpened());

        columnSupplier.get()
            .setColumnType(columnType);

        AwaitilityWrapper.createDefault()
            .until(() -> !columnSupplier.get().isColumnTypeSelectorOpened());
    }
}
