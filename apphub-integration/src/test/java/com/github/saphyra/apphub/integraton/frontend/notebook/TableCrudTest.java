package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TableCrudTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category";
    private static final String TABLE_TITLE = "table";
    private static final String TABLE_HEAD_1 = "table-head-1";
    private static final String TABLE_HEAD_2 = "table-head-2";
    private static final String COLUMN_1 = "column-1";
    private static final String COLUMN_2 = "column-2";
    private static final String NEW_TABLE_TITLE = "new-table";

    @Test(groups = "notebook")
    public void checklistTableCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.CHECKLIST_TABLE);

        //Create - Blank title
        NewTableActions.fillTitle(driver, " ");
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Create - Has Blank column name
        NewTableActions.fillTitle(driver, TABLE_TITLE);
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Name of the column must not be blank.");

        //-- Add row
        NewTableActions.newRow(driver);

        assertThat(NewTableActions.getRows(driver)).hasSize(2);

        //-- Remove row
        NewTableActions.getRows(driver)
            .get(0)
            .remove();

        assertThat(NewTableActions.getRows(driver)).hasSize(1);

        //-- Add column
        NewTableActions.newColumn(driver);

        assertThat(NewTableActions.getTableHeads(driver)).hasSize(2);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).hasSize(2);

        //-- Remove column
        NewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        assertThat(NewTableActions.getTableHeads(driver)).hasSize(1);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).hasSize(1);

        //-- Check row
        NewTableActions.getRows(driver)
            .get(0)
            .check();

        assertThat(NewTableActions.getRows(driver).get(0).isChecked()).isTrue();

        //-- Move column - Right
        NewTableActions.newColumn(driver);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_1);
        NewTableActions.getTableHeads(driver)
            .get(1)
            .setValue(TABLE_HEAD_2);
        NewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        NewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(1)
            .setValue(COLUMN_2);

        NewTableActions.getTableHeads(driver)
            .get(0)
            .moveRight();

        assertThat(NewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_2, TABLE_HEAD_1);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move column - Left
        NewTableActions.getTableHeads(driver)
            .get(1)
            .moveLeft();

        assertThat(NewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1, TABLE_HEAD_2);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_1, COLUMN_2);

        //-- Move row - Down
        NewTableActions.getTableHeads(driver)
            .get(1)
            .remove();

        NewTableActions.newRow(driver);
        NewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);

        NewTableActions.getRows(driver)
            .get(0)
            .moveDown();

        assertThat(NewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move row - Up
        NewTableActions.getRows(driver)
            .get(1)
            .moveUp();

        assertThat(NewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);

        //Create
        ParentSelectorActions.selectParent(driver, CATEGORY_TITLE);
        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE));

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open(() -> NotebookActions.findListItemByTitle(driver, TABLE_TITLE).isPresent());

        //View
        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .open(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-table"))).isPresent());

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1);
        assertThat(ViewTableActions.getRows(driver)).extracting(TableRow::isChecked).containsExactly(true, false);
        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);

        //Edit - Blank title
        ViewTableActions.enableEditing(driver);

        ViewTableActions.setTitle(driver, " ");

        ViewTableActions.saveChanges(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Edit - Blank column name
        ViewTableActions.setTitle(driver, NEW_TABLE_TITLE);

        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(" ");

        ViewTableActions.saveChanges(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Name of the column must not be blank.");

        //Edit - Discard
        ViewTableActions.discardChanges(driver);

        assertThat(ViewTableActions.isEditingEnabled(driver)).isFalse();
        AwaitilityWrapper.createDefault()
            .until(() -> ViewTableActions.getTitle(driver).equals(TABLE_TITLE))
            .assertTrue("Title is not reset");
        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1);

        //-- Add row
        ViewTableActions.enableEditing(driver);

        ViewTableActions.newRow(driver);

        assertThat(ViewTableActions.getRows(driver)).hasSize(3);

        //-- Remove row
        ViewTableActions.getRows(driver)
            .get(0)
            .remove();

        assertThat(ViewTableActions.getRows(driver)).hasSize(2);

        //-- Add column
        ViewTableActions.newColumn(driver);

        assertThat(ViewTableActions.getTableHeads(driver)).hasSize(2);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).hasSize(2);

        //-- Check row
        ViewTableActions.getRows(driver)
            .get(0)
            .check();

        assertThat(ViewTableActions.getRows(driver)).extracting(TableRow::isChecked).containsExactly(true, false);

        //-- Move column - Right
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_1);
        ViewTableActions.getTableHeads(driver)
            .get(1)
            .setValue(TABLE_HEAD_2);
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(1)
            .setValue(COLUMN_2);

        ViewTableActions.getTableHeads(driver)
            .get(0)
            .moveRight();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_2, TABLE_HEAD_1);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move column - Left
        ViewTableActions.getTableHeads(driver)
            .get(1)
            .moveLeft();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1, TABLE_HEAD_2);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_1, COLUMN_2);

        //-- Remove column
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        assertThat(ViewTableActions.getTableHeads(driver)).hasSize(1);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).hasSize(1);

        //-- Move row - Down
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        ViewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);

        ViewTableActions.getRows(driver)
            .get(0)
            .moveDown();

        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move row - Up
        ViewTableActions.getRows(driver)
            .get(1)
            .moveUp();

        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);

        //Edit
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_2);
        ViewTableActions.getRows(driver)
            .get(0)
            .uncheck();
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);
        ViewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        ViewTableActions.setTitle(driver, NEW_TABLE_TITLE);

        ViewTableActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ViewTableActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled");

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_2);
        assertThat(ViewTableActions.getRows(driver)).extracting(TableRow::isChecked).containsExactly(false, false);
        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);

        //Check row
        ViewTableActions.getRows(driver)
            .get(0)
            .check();

        assertThat(ViewTableActions.getRows(driver)).extracting(TableRow::isChecked).containsExactly(true, false);

        //Uncheck row
        ViewTableActions.getRows(driver)
            .get(0)
            .uncheck();

        assertThat(ViewTableActions.getRows(driver)).extracting(TableRow::isChecked).containsExactly(false, false);

        //Delete checked
        ViewTableActions.getRows(driver)
            .get(0)
            .check();

        ViewTableActions.deleteChecked(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> ViewTableActions.getRows(driver).size() == 1)
            .assertTrue("Checked row is not deleted.");

        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1);

        //Delete
        ViewTableActions.close(driver);

        NotebookActions.findListItemByTitleValidated(driver, NEW_TABLE_TITLE)
            .delete(driver);
        NotebookActions.confirmListItemDeletion(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Checklist table is not deleted.");
    }

    @Test(groups = "notebook")
    public void tableCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.TABLE);

        //Create - Blank title
        NewTableActions.fillTitle(driver, " ");
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Create - Has Blank column name
        NewTableActions.fillTitle(driver, TABLE_TITLE);
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Name of the column must not be blank.");

        //-- Add row
        NewTableActions.newRow(driver);

        assertThat(NewTableActions.getRows(driver)).hasSize(2);

        //-- Remove row
        NewTableActions.getRows(driver)
            .get(0)
            .remove();

        assertThat(NewTableActions.getRows(driver)).hasSize(1);

        //-- Add column
        NewTableActions.newColumn(driver);

        assertThat(NewTableActions.getTableHeads(driver)).hasSize(2);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).hasSize(2);

        //-- Remove column
        NewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        assertThat(NewTableActions.getTableHeads(driver)).hasSize(1);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).hasSize(1);

        //-- Move column - Right
        NewTableActions.newColumn(driver);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_1);
        NewTableActions.getTableHeads(driver)
            .get(1)
            .setValue(TABLE_HEAD_2);
        NewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        NewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(1)
            .setValue(COLUMN_2);

        NewTableActions.getTableHeads(driver)
            .get(0)
            .moveRight();

        assertThat(NewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_2, TABLE_HEAD_1);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move column - Left
        NewTableActions.getTableHeads(driver)
            .get(1)
            .moveLeft();

        assertThat(NewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1, TABLE_HEAD_2);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_1, COLUMN_2);

        //-- Move row - Down
        NewTableActions.getTableHeads(driver)
            .get(1)
            .remove();

        NewTableActions.newRow(driver);
        NewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);

        NewTableActions.getRows(driver)
            .get(0)
            .moveDown();

        assertThat(NewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move row - Up
        NewTableActions.getRows(driver)
            .get(1)
            .moveUp();

        assertThat(NewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);

        //Create
        ParentSelectorActions.selectParent(driver, CATEGORY_TITLE);
        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE));

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open(() -> NotebookActions.findListItemByTitle(driver, TABLE_TITLE).isPresent());

        //View
        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .open(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-table"))).isPresent());

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1);
        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);

        //Edit - Blank title
        ViewTableActions.enableEditing(driver);

        ViewTableActions.setTitle(driver, " ");

        ViewTableActions.saveChanges(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Edit - Blank column name
        ViewTableActions.setTitle(driver, NEW_TABLE_TITLE);

        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(" ");

        ViewTableActions.saveChanges(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Name of the column must not be blank.");

        //Edit - Discard
        ViewTableActions.discardChanges(driver);

        assertThat(ViewTableActions.isEditingEnabled(driver)).isFalse();
        AwaitilityWrapper.createDefault()
            .until(() -> ViewTableActions.getTitle(driver).equals(TABLE_TITLE))
            .assertTrue("Title is not reset");
        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1);

        //-- Add row
        ViewTableActions.enableEditing(driver);

        ViewTableActions.newRow(driver);

        assertThat(ViewTableActions.getRows(driver)).hasSize(3);

        //-- Remove row
        ViewTableActions.getRows(driver)
            .get(0)
            .remove();

        assertThat(ViewTableActions.getRows(driver)).hasSize(2);

        //-- Add column
        ViewTableActions.newColumn(driver);

        assertThat(ViewTableActions.getTableHeads(driver)).hasSize(2);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).hasSize(2);

        //-- Move column - Right
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_1);
        ViewTableActions.getTableHeads(driver)
            .get(1)
            .setValue(TABLE_HEAD_2);
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(1)
            .setValue(COLUMN_2);

        ViewTableActions.getTableHeads(driver)
            .get(0)
            .moveRight();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_2, TABLE_HEAD_1);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move column - Left
        ViewTableActions.getTableHeads(driver)
            .get(1)
            .moveLeft();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1, TABLE_HEAD_2);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_1, COLUMN_2);

        //-- Remove column
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        assertThat(ViewTableActions.getTableHeads(driver)).hasSize(1);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).hasSize(1);

        //-- Move row - Down
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        ViewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);

        ViewTableActions.getRows(driver)
            .get(0)
            .moveDown();

        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);

        //-- Move row - Up
        ViewTableActions.getRows(driver)
            .get(1)
            .moveUp();

        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);

        //Edit
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_2);
        ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);
        ViewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        ViewTableActions.setTitle(driver, NEW_TABLE_TITLE);

        ViewTableActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ViewTableActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled");

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_2);
        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);

        //Delete
        ViewTableActions.close(driver);

        NotebookActions.findListItemByTitleValidated(driver, NEW_TABLE_TITLE)
            .delete(driver);
        NotebookActions.confirmListItemDeletion(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Checklist table is not deleted.");
    }

    @Test(groups = "notebook")
    public void convertTableToChecklistTable() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.TABLE);

        NewTableActions.fillTitle(driver, TABLE_TITLE);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_1);
        NewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .setValue(COLUMN_1);
        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Table was not saved");

        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .pin(driver);

        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .open();

        ViewTableActions.convertTableToChecklistTable(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> ViewTableActions.getRows(driver).get(0).isChecklistRow())
            .assertTrue("Table is not converted to ChecklistTable");

        assertThat(NotebookActions.getPinnedItems(driver).get(0).getType()).isEqualTo(ListItemType.CHECKLIST_TABLE);
    }
}
