package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TableActions {
    private static final By COLUMNS_OF_ROW = By.cssSelector(":scope .table-column");
    private static final By INPUT_FIELD_OF_COLUMN = By.cssSelector(":scope .table-column-content");
    private static final By REMOVE_COLUMN_BUTTON = By.cssSelector(":scope .view-table-operations-button-wrapper button:nth-child(3)");
    private static final By REMOVE_ROW_BUTTON = By.cssSelector(":scope .view-table-operations-button-wrapper button:nth-child(3)");
    private static final By MOVE_COLUMN_LEFT_BUTTON = By.cssSelector(":scope .view-table-operations-button-wrapper button:nth-child(1)");
    private static final By MOVE_ROW_UP = By.cssSelector(":scope .view-table-operations-button-wrapper button:nth-child(1)");

    public static void createTable(WebDriver driver, String tableTitle, List<String> columnNames, List<List<String>> rows, String... categories) {
        if (!isCreateTableWindowDisplayed(driver)) {
            openCreateTableWindow(driver);
        }

        fillNewTableTitle(driver, tableTitle);
        selectCategoryForNewTable(driver, categories);

        while (NotebookPage.columnNamesForNewTable(driver).size() < columnNames.size()) {
            addColumnToNewTable(driver);
        }

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            fillColumnName(driver, columnIndex, columnNames.get(columnIndex));
        }

        while (NotebookPage.rowsForNewTable(driver).size() < rows.size()) {
            addRowToNewTable(driver);
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> columns = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                fillColumn(driver, rowIndex, columnIndex, columns.get(columnIndex));
            }
        }

        submitCreateTableForm(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> !isCreateTableWindowDisplayed(driver));
    }

    private static void fillColumn(WebDriver driver, int rowIndex, int columnIndex, String columnValue) {
        WebElement inputField = NotebookPage.rowsForNewTable(driver)
            .get(rowIndex)
            .findElements(COLUMNS_OF_ROW)
            .get(columnIndex)
            .findElement(INPUT_FIELD_OF_COLUMN);

        clearAndFill(inputField, columnValue);
    }

    private static void addRowToNewTable(WebDriver driver) {
        NotebookPage.addRowToNewTableButton(driver).click();
    }

    public static void openCreateTableWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateTableWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> isCreateTableWindowDisplayed(driver));
    }

    public static void submitCreateTableForm(WebDriver driver) {
        NotebookPage.saveNewTableButton(driver).click();
    }

    public static boolean isCreateTableWindowDisplayed(WebDriver driver) {
        return NotebookPage.createTableWindow(driver).isDisplayed();
    }

    public static void fillNewTableTitle(WebDriver driver, String title) {
        clearAndFill(NotebookPage.newTableTitleInput(driver), title);
    }

    public static void addColumnToNewTable(WebDriver driver) {
        NotebookPage.addColumnToNewTableButton(driver).click();
    }

    public static void fillColumnName(WebDriver driver, int columnIndex, String columnName) {
        List<WebElement> columnNames = NotebookPage.columnNamesForNewTable(driver);

        clearAndFill(columnNames.get(columnIndex), columnName);
    }

    public static void selectCategoryForNewTable(WebDriver driver, String... categories) {
        new Actions(driver)
            .moveToElement(NotebookPage.createTableSelectedCategoryWrapper(driver))
            .perform();
        for (String parentTitle : categories) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.availableParentsForNewTable(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }
    }

    public static void openTable(WebDriver driver, String tableTitle) {
        DetailedListActions.findDetailedItem(driver, tableTitle).open();

        AwaitilityWrapper.createDefault()
            .until(() -> isViewTableWindowOpened(driver))
            .assertTrue();
    }

    private static boolean isViewTableWindowOpened(WebDriver driver) {
        return NotebookPage.viewTableWindow(driver).isDisplayed();
    }

    public static void enableEditing(WebDriver driver) {
        assertThat(isViewTableWindowOpened(driver)).isTrue();
        if (!isEditingEnabled(driver)) {
            NotebookPage.editTableButton(driver).click();
        }
        assertThat(isEditingEnabled(driver)).isTrue();
    }

    public static boolean isEditingEnabled(WebDriver driver) {
        return !NotebookPage.editTableButton(driver)
            .isDisplayed();
    }

    public static void editTableTitle(WebDriver driver, String newTitle) {
        assertThat(isEditingEnabled(driver)).isTrue();
        WebElement title = NotebookPage.viewTableTitle(driver);
        assertThat(title.getAttribute("contenteditable")).isEqualTo("true");
        clearAndFill(title, newTitle);
    }

    public static void saveChanges(WebDriver driver) {
        NotebookPage.saveEditedTableButton(driver).click();
    }

    public static void editColumnName(WebDriver driver, int columnIndex, String newColumnName) {
        WebElement columnInput = NotebookPage.columnNamesForViewTable(driver).get(columnIndex);
        clearAndFill(columnInput, newColumnName);
    }

    public static void editTable(WebDriver driver, String newTitle, List<String> columnNames, List<List<String>> rows) {
        if (!isEditingEnabled(driver)) {
            enableEditing(driver);
        }

        editTableTitle(driver, newTitle);

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            editColumnName(driver, columnIndex, columnNames.get(columnIndex));
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> columns = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                editColumnValue(driver, rowIndex, columnIndex, columns.get(columnIndex));
            }
        }
    }

    public static void editColumnValue(WebDriver driver, int rowIndex, int columnIndex, String newValue) {
        WebElement inputField = NotebookPage.rowsForViewTable(driver)
            .get(rowIndex)
            .findElements(COLUMNS_OF_ROW)
            .get(columnIndex)
            .findElement(INPUT_FIELD_OF_COLUMN);

        clearAndFill(inputField, newValue);
    }

    public static void discardChanges(WebDriver driver) {
        assertThat(isEditingEnabled(driver)).isTrue();
        NotebookPage.discardEditTableButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> !isEditingEnabled(driver));
    }

    public static void verifyViewTable(WebDriver driver, String tableTitle, List<String> columnNames, List<List<String>> rows) {
        assertThat(NotebookPage.viewTableTitle(driver).getText()).isEqualTo(tableTitle);

        List<WebElement> columnNameValues = NotebookPage.columnNamesForViewTable(driver);
        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            assertThat(columnNameValues.get(columnIndex).getAttribute("value")).isEqualTo(columnNames.get(columnIndex));
        }

        List<WebElement> rowValues = NotebookPage.rowsForViewTable(driver);
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> columns = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                String currentValue = rowValues.get(rowIndex)
                    .findElements(COLUMNS_OF_ROW)
                    .get(columnIndex)
                    .findElement(INPUT_FIELD_OF_COLUMN)
                    .getText();
                assertThat(currentValue).isEqualTo(columns.get(columnIndex));
            }
        }
    }

    public static void addColumnToEditTable(WebDriver driver) {
        NotebookPage.addColumnToEditTableButton(driver).click();
    }

    public static void addRowToEditTable(WebDriver driver) {
        NotebookPage.addRowToEditTableButton(driver).click();
    }

    public static void removeColumnFromEditTable(WebDriver driver, int columnIndex) {
        WebElement columnName = NotebookPage.columnHeadsForEditTable(driver).get(columnIndex);
        columnName.findElement(REMOVE_COLUMN_BUTTON).click();
    }

    public static void removeRowFromEditTable(WebDriver driver, int rowIndex) {
        WebElement row = NotebookPage.rowsForViewTable(driver).get(rowIndex);
        row.findElement(REMOVE_ROW_BUTTON).click();
    }

    public static void moveColumnLeft(WebDriver driver, int columnIndex) {
        WebElement columnName = NotebookPage.columnHeadsForEditTable(driver).get(columnIndex);
        columnName.findElement(MOVE_COLUMN_LEFT_BUTTON).click();
    }

    public static void moveRowUp(WebDriver driver, int rowIndex) {
        WebElement row = NotebookPage.rowsForViewTable(driver).get(rowIndex);
        row.findElement(MOVE_ROW_UP).click();
    }
}
