package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRow;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ChecklistTableActions {
    private static final By COLUMNS_OF_ROW = By.cssSelector(":scope .table-column");
    private static final By INPUT_FIELD_OF_COLUMN = By.cssSelector(":scope .table-column-content");
    private static final By REMOVE_COLUMN_BUTTON = By.cssSelector(":scope .view-checklist-table-operations-button-wrapper button:nth-child(3)");
    private static final By REMOVE_ROW_BUTTON = By.cssSelector(":scope .view-checklist-table-operations-button-wrapper button:nth-child(3)");
    private static final By MOVE_COLUMN_LEFT_BUTTON = By.cssSelector(":scope .view-checklist-table-operations-button-wrapper button:nth-child(1)");
    private static final By MOVE_ROW_UP = By.cssSelector(":scope .view-checklist-table-operations-button-wrapper button:nth-child(1)");
    private static final By CREATE_CHECKLIST_TABLE_ROW_CHECKED_INPUT = By.cssSelector(":scope .create-checklist-table-checked-cell input[type=checkbox]");
    private static final By VIEW_CHECKLIST_TABLE_ROW_CHECKED_INPUT = By.cssSelector(":scope .view-checklist-table-checked-cell input[type=checkbox]");

    public static void createChecklistTable(WebDriver driver, String tableTitle, List<String> columnNames, List<ChecklistTableRow> rows, String... categories) {
        if (!isCreateChecklistTableWindowDisplayed(driver)) {
            openCreateChecklistTableWindow(driver);
        }

        selectCategoryForNewChecklistTable(driver, categories);
        fillNewChecklistTableTitle(driver, tableTitle);

        while (NotebookPage.columnNamesForNewChecklistTable(driver).size() < columnNames.size()) {
            addColumnToNewChecklistTable(driver);
        }

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            fillColumnName(driver, columnIndex, columnNames.get(columnIndex));
        }

        while (NotebookPage.rowsForNewChecklistTable(driver).size() < rows.size()) {
            addRowToNewChecklistTable(driver);
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            ChecklistTableRow row = rows.get(rowIndex);
            List<String> columns = row.getColumns();

            if (row.isChecked()) {
                WebElement checkedInput = NotebookPage.rowsForNewChecklistTable(driver)
                    .get(rowIndex)
                    .findElement(CREATE_CHECKLIST_TABLE_ROW_CHECKED_INPUT);

                if (!checkedInput.isSelected()) {
                    checkedInput.click();
                    assertThat(checkedInput.isSelected()).isTrue();
                }
            }

            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                fillColumn(driver, rowIndex, columnIndex, columns.get(columnIndex));
            }
        }

        submitCreateChecklistTableForm(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> !isCreateChecklistTableWindowDisplayed(driver));
    }

    private static void fillColumn(WebDriver driver, int rowIndex, int columnIndex, String columnValue) {
        WebElement inputField = NotebookPage.rowsForNewChecklistTable(driver)
            .get(rowIndex)
            .findElements(COLUMNS_OF_ROW)
            .get(columnIndex)
            .findElement(INPUT_FIELD_OF_COLUMN);

        clearAndFill(inputField, columnValue);
    }

    private static void addRowToNewChecklistTable(WebDriver driver) {
        NotebookPage.addRowToNewChecklistTableButton(driver).click();
    }

    public static void openCreateChecklistTableWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateChecklistTableWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> isCreateChecklistTableWindowDisplayed(driver));
    }

    public static boolean isCreateChecklistTableWindowDisplayed(WebDriver driver) {
        return NotebookPage.createChecklistTableWindow(driver).isDisplayed();
    }

    public static void submitCreateChecklistTableForm(WebDriver driver) {
        NotebookPage.saveNewChecklistTableButton(driver).click();
    }

    public static void fillNewChecklistTableTitle(WebDriver driver, String title) {
        clearAndFill(NotebookPage.newChecklistTableTitleInput(driver), title);
    }

    public static void addColumnToNewChecklistTable(WebDriver driver) {
        NotebookPage.addColumnToNewChecklistTableButton(driver).click();
    }

    public static void fillColumnName(WebDriver driver, int columnIndex, String columnName) {
        List<WebElement> columnNames = NotebookPage.columnNamesForNewChecklistTable(driver);

        clearAndFill(columnNames.get(columnIndex), columnName);
    }

    public static void selectCategoryForNewChecklistTable(WebDriver driver, String... categories) {
        new Actions(driver)
            .moveToElement(NotebookPage.createChecklistTableSelectedCategoryWrapper(driver))
            .perform();
        for (String parentTitle : categories) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.availableParentsForNewChecklistTable(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }
        new Actions(driver)
            .moveToElement(NotebookPage.saveNewChecklistTableButton(driver))
            .perform();
    }

    public static void openChecklistTable(WebDriver driver, String tableTitle) {
        DetailedListActions.findDetailedItem(driver, tableTitle).open();

        AwaitilityWrapper.createDefault()
            .until(() -> isViewChecklistTableWindowOpened(driver))
            .assertTrue();
    }

    public static boolean isViewChecklistTableWindowOpened(WebDriver driver) {
        return NotebookPage.viewChecklistTableWindow(driver).isDisplayed();
    }

    public static void enableEditing(WebDriver driver) {
        assertThat(isViewChecklistTableWindowOpened(driver)).isTrue();
        if (!isEditingEnabled(driver)) {
            NotebookPage.editChecklistTableButton(driver).click();
        }
        assertThat(isEditingEnabled(driver)).isTrue();
    }

    public static boolean isEditingEnabled(WebDriver driver) {
        return !NotebookPage.editChecklistTableButton(driver)
            .isDisplayed();
    }

    public static void editChecklistTableTitle(WebDriver driver, String newTitle) {
        assertThat(isEditingEnabled(driver)).isTrue();
        WebElement title = NotebookPage.viewChecklistTableTitle(driver);
        assertThat(title.getAttribute("contenteditable")).isEqualTo("true");
        clearAndFill(title, newTitle);
    }

    public static void saveChanges(WebDriver driver) {
        NotebookPage.saveEditedChecklistTableButton(driver).click();
    }

    public static void editColumnName(WebDriver driver, int columnIndex, String newColumnName) {
        WebElement columnInput = NotebookPage.columnNamesForViewChecklistTable(driver).get(columnIndex);
        clearAndFill(columnInput, newColumnName);
    }

    public static void editChecklistTable(WebDriver driver, String newTitle, List<String> columnNames, List<ChecklistTableRow> rows) {
        if (!isEditingEnabled(driver)) {
            enableEditing(driver);
        }

        editChecklistTableTitle(driver, newTitle);

        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            editColumnName(driver, columnIndex, columnNames.get(columnIndex));
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            ChecklistTableRow row = rows.get(rowIndex);

            WebElement checkedInput = NotebookPage.rowsForViewChecklistTable(driver)
                .get(rowIndex)
                .findElement(VIEW_CHECKLIST_TABLE_ROW_CHECKED_INPUT);

            if (checkedInput.isSelected() != row.isChecked()) {
                checkedInput.click();
                assertThat(checkedInput.isSelected()).isEqualTo(row.isChecked());
            }

            List<String> columns = row.getColumns();
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                editColumnValue(driver, rowIndex, columnIndex, columns.get(columnIndex));
            }
        }
    }

    public static void editColumnValue(WebDriver driver, int rowIndex, int columnIndex, String newValue) {
        WebElement inputField = NotebookPage.rowsForViewChecklistTable(driver)
            .get(rowIndex)
            .findElements(COLUMNS_OF_ROW)
            .get(columnIndex)
            .findElement(INPUT_FIELD_OF_COLUMN);

        clearAndFill(inputField, newValue);
    }

    public static void discardChanges(WebDriver driver) {
        assertThat(isEditingEnabled(driver)).isTrue();
        NotebookPage.discardEditChecklistTableButton(driver).click();
        CommonPageActions.confirmConfirmationDialog(driver, "discard-confirmation-dialog");
        AwaitilityWrapper.createDefault()
            .until(() -> !isEditingEnabled(driver));
    }

    public static void verifyViewChecklistTable(WebDriver driver, String tableTitle, List<String> columnNames, List<ChecklistTableRow> rows) {
        assertThat(NotebookPage.viewChecklistTableTitle(driver).getText()).isEqualTo(tableTitle);

        List<WebElement> columnNameValues = NotebookPage.columnNamesForViewChecklistTable(driver);
        for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
            assertThat(columnNameValues.get(columnIndex).getAttribute("value")).isEqualTo(columnNames.get(columnIndex));
        }

        List<WebElement> rowValues = NotebookPage.rowsForViewChecklistTable(driver);
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            ChecklistTableRow row = rows.get(rowIndex);

            WebElement checkedInput = NotebookPage.rowsForViewChecklistTable(driver)
                .get(rowIndex)
                .findElement(VIEW_CHECKLIST_TABLE_ROW_CHECKED_INPUT);

            assertThat(checkedInput.isSelected()).isEqualTo(row.isChecked());

            List<String> columns = row.getColumns();
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

    public static void addRowToEditChecklistTable(WebDriver driver) {
        NotebookPage.addRowToEditChecklistTableButton(driver).click();
    }

    public static void removeColumnFromEditChecklistTable(WebDriver driver, int columnIndex) {
        WebElement columnName = NotebookPage.columnHeadsForEditChecklistTable(driver).get(columnIndex);
        columnName.findElement(REMOVE_COLUMN_BUTTON).click();
    }

    public static void removeRowFromEditChecklistTable(WebDriver driver, int rowIndex) {
        WebElement row = NotebookPage.rowsForViewChecklistTable(driver).get(rowIndex);
        row.findElement(REMOVE_ROW_BUTTON).click();
    }

    public static void moveColumnLeft(WebDriver driver, int columnIndex) {
        WebElement columnName = NotebookPage.columnHeadsForEditChecklistTable(driver).get(columnIndex);
        columnName.findElement(MOVE_COLUMN_LEFT_BUTTON).click();
    }

    public static void moveRowUp(WebDriver driver, int rowIndex) {
        WebElement row = NotebookPage.rowsForViewChecklistTable(driver).get(rowIndex);
        row.findElement(MOVE_ROW_UP).click();
    }

    public static void addColumnToEditChecklistTable(WebDriver driver) {
        NotebookPage.addColumnToEditChecklistTableButton(driver).click();
    }

    public static void viewChecklistTableSetRowStatus(WebDriver driver, int rowIndex, boolean status) {
        WebElement checkedInput = NotebookPage.rowsForViewChecklistTable(driver)
            .get(rowIndex)
            .findElement(VIEW_CHECKLIST_TABLE_ROW_CHECKED_INPUT);

        if (checkedInput.isSelected() != status) {
            checkedInput.click();
            assertThat(checkedInput.isSelected()).isEqualTo(status);
        }
    }

    public static void closeWindow(WebDriver driver) {
        NotebookPage.viewChecklistTableCloseButton(driver).click();
    }

    public static void deleteCheckedChecklistTableItems(WebDriver driver) {
        NotebookPage.deleteCheckedChecklistTableItemsButton(driver)
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, NotebookPage.DELETE_CHECKED_ITEMS_CONFIRMATION_DIALOG_ID);
    }
}
