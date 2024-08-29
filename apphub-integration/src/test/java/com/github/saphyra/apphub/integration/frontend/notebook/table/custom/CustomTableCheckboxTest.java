package com.github.saphyra.apphub.integration.frontend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableRow;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.CheckedTableColumn;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableCheckboxTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";

    @Test(groups = {"fe", "notebook"})
    public void customTableCheckboxCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        createCustomTableWithCheckedCell(driver);
        openTable(driver);
        toggleStatus(driver);
        editColumn(driver);
    }

    private void toggleStatus(WebDriver driver) {
        getColumnAsChecked(ViewTableActions.getRows(driver))
            .setChecked(false);

        ViewTableActions.close(driver);
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open();

        assertThat(getColumnAsChecked(ViewTableActions.getRows(driver)).isChecked()).isFalse();
    }

    private static void editColumn(WebDriver driver) {
        ViewTableActions.enableEditing(driver);
        getColumnAsChecked(ViewTableActions.getRows(driver))
            .setChecked(true);
        assertThat(getColumnAsChecked(ViewTableActions.getRows(driver)).isChecked()).isTrue();

        ViewTableActions.saveChanges(driver);

        assertThat(getColumnAsChecked(ViewTableActions.getRows(driver)).isChecked()).isTrue();
    }

    private static void openTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open();
        CheckedTableColumn checkedColumn = getColumnAsChecked(ViewTableActions.getRows(driver));
        assertThat(checkedColumn.isChecked()).isTrue();
    }

    private static CheckedTableColumn getColumnAsChecked(List<TableRow> driver) {
        return driver.get(0).getColumns().get(0).as(ColumnType.CHECKBOX);
    }

    private static void createCustomTableWithCheckedCell(WebDriver driver) {
        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.CUSTOM_TABLE);

        NewTableActions.fillTitle(driver, TITLE);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_CONTENT);

        NewTableActions.setColumnType(driver, 0, 0, ColumnType.CHECKBOX);

        CheckedTableColumn tableColumn = getColumnAsChecked(NewTableActions.getRows(driver));
        tableColumn.setChecked(true);

        assertThat(tableColumn.isChecked()).isTrue();

        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Notebook page is not opened.");
    }
}
