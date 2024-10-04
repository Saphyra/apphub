package com.github.saphyra.apphub.integration.frontend.notebook.table;

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
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
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

    @Test(groups = {"fe", "notebook"})
    public void tableCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_TITLE);

        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.TABLE);

        create_blankTitle(driver);
        create_hasBlankColumnName(driver);
        create_addRow(driver);
        create_removeRow(driver);
        create_addColumn(driver);
        create_removeColumn(driver);
        create_moveColumnRight(driver);
        create_moveColumnLeft(driver);
        create_moveRowDown(driver);
        create_moveRowUp(driver);
        create(driver);
        view(driver);
        edit_blankTitle(driver);
        edit_blankColumnName(driver);
        edit_discard(driver);
        edit_addRow(driver);
        edit_removeRow(driver);
        edit_addColumn(driver);
        edit_moveColumnRight(driver);
        edit_moveColumnLeft(driver);
        edit_removeColumn(driver);
        edit_moveRowDown(driver);
        edit_moveRowUp(driver);
        edit(driver);
        delete(driver);
    }

    private static void create_blankTitle(WebDriver driver) {
        NewTableActions.fillTitle(driver, " ");
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);
    }

    private static void create_hasBlankColumnName(WebDriver driver) {
        NewTableActions.fillTitle(driver, TABLE_TITLE);
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_COLUMN_NAME_MUST_NOT_BE_BLANK);
    }

    private static void create_addRow(WebDriver driver) {
        NewTableActions.addRowToEnd(driver);

        assertThat(NewTableActions.getRows(driver)).hasSize(2);
    }

    private static void create_removeRow(WebDriver driver) {
        NewTableActions.getRows(driver)
            .get(0)
            .remove();

        assertThat(NewTableActions.getRows(driver)).hasSize(1);
    }

    private static void create_addColumn(WebDriver driver) {
        NewTableActions.addColumnToEnd(driver);

        assertThat(NewTableActions.getTableHeads(driver)).hasSize(2);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).hasSize(2);
    }

    private static void create_removeColumn(WebDriver driver) {
        NewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        assertThat(NewTableActions.getTableHeads(driver)).hasSize(1);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).hasSize(1);
    }

    private static void create_moveColumnRight(WebDriver driver) {
        NewTableActions.addColumnToEnd(driver);
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
    }

    private static void create_moveColumnLeft(WebDriver driver) {
        NewTableActions.getTableHeads(driver)
            .get(1)
            .moveLeft();

        assertThat(NewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1, TABLE_HEAD_2);
        assertThat(NewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_1, COLUMN_2);
    }

    private static void create_moveRowDown(WebDriver driver) {
        NewTableActions.getTableHeads(driver)
            .get(1)
            .remove();

        NewTableActions.addRowToEnd(driver);
        NewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .setValue(COLUMN_2);

        NewTableActions.getRows(driver)
            .get(0)
            .moveDown();

        assertThat(NewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_2, COLUMN_1);
    }

    private static void create_moveRowUp(WebDriver driver) {
        NewTableActions.getRows(driver)
            .get(1)
            .moveUp();

        assertThat(NewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);
    }

    private static void create(WebDriver driver) {
        ParentSelectorActions.selectParent(driver, CATEGORY_TITLE);
        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE));

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open(() -> NotebookActions.findListItemByTitle(driver, TABLE_TITLE).isPresent());
    }

    private static void view(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .open(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-table"))).isPresent());

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1);
        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);
    }

    private static void edit_blankTitle(WebDriver driver) {
        ViewTableActions.enableEditing(driver);

        ViewTableActions.setTitle(driver, " ");

        ViewTableActions.saveChanges(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);
    }

    private static void edit_blankColumnName(WebDriver driver) {
        ViewTableActions.setTitle(driver, NEW_TABLE_TITLE);

        ViewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(" ");

        ViewTableActions.saveChanges(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_COLUMN_NAME_MUST_NOT_BE_BLANK);
    }

    private static void edit_discard(WebDriver driver) {
        ViewTableActions.discardChanges(driver);

        assertThat(ViewTableActions.isEditingEnabled(driver)).isFalse();
        AwaitilityWrapper.createDefault()
            .until(() -> ViewTableActions.getTitle(driver).equals(TABLE_TITLE))
            .assertTrue("Title is not reset");
        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1);
    }

    private static void edit_addRow(WebDriver driver) {
        ViewTableActions.enableEditing(driver);

        ViewTableActions.addRowToEnd(driver);

        assertThat(ViewTableActions.getRows(driver)).hasSize(3);
    }

    private static void edit_removeRow(WebDriver driver) {
        ViewTableActions.getRows(driver)
            .get(0)
            .remove();

        assertThat(ViewTableActions.getRows(driver)).hasSize(2);
    }

    private static void edit_addColumn(WebDriver driver) {
        ViewTableActions.addColumnToEnd(driver);

        assertThat(ViewTableActions.getTableHeads(driver)).hasSize(2);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).hasSize(2);
    }

    private static void edit_moveColumnRight(WebDriver driver) {
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
    }

    private static void edit_moveColumnLeft(WebDriver driver) {
        ViewTableActions.getTableHeads(driver)
            .get(1)
            .moveLeft();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD_1, TABLE_HEAD_2);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).extracting(TableColumn::getValue).containsExactly(COLUMN_1, COLUMN_2);
    }

    private static void edit_removeColumn(WebDriver driver) {
        ViewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        assertThat(ViewTableActions.getTableHeads(driver)).hasSize(1);
        assertThat(ViewTableActions.getRows(driver).get(0).getColumns()).hasSize(1);
    }

    private static void edit_moveRowDown(WebDriver driver) {
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
    }

    private static void edit_moveRowUp(WebDriver driver) {
        ViewTableActions.getRows(driver)
            .get(1)
            .moveUp();

        assertThat(ViewTableActions.getRows(driver)).extracting(tableRow -> tableRow.getColumns().get(0).getValue()).containsExactly(COLUMN_1, COLUMN_2);
    }

    private static void edit(WebDriver driver) {
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
    }

    private static void delete(WebDriver driver) {
        ViewTableActions.close(driver);

        NotebookActions.findListItemByTitleValidated(driver, NEW_TABLE_TITLE)
            .delete(driver);
        NotebookActions.confirmListItemDeletion(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Checklist table is not deleted.");
    }
}
