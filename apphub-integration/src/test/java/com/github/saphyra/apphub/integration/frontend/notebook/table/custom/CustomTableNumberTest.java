package com.github.saphyra.apphub.integration.frontend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableRow;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.NumberTableColumn;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableNumberTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final int VALUE = 34;
    private static final int STEP = 2;
    private static final int NEW_VALUE = 35;
    private static final int NEW_STEP = 3;

    @Test(groups = {"fe", "notebook"})
    public void customTableNumberCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        createCustomTableWithNumberCell(driver);
        openTable(driver);
        editColumn(driver);
    }

    private static void editColumn(WebDriver driver) {
        ViewTableActions.enableEditing(driver);

        NumberTableColumn column = getColumnAsNumber(getRowsWithWait(driver));
        column.setValue(NEW_VALUE);
        column.setStep(NEW_STEP);

        assertThat(column.getValue()).isEqualTo(String.valueOf(NEW_VALUE));
        assertThat(column.getStep()).isEqualTo(String.valueOf(NEW_STEP));

        ViewTableActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ViewTableActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled.");

        assertThat(getColumnAsNumber(getRowsWithWait(driver)).getValue()).isEqualTo(String.valueOf(NEW_VALUE));
    }

    private static void openTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open(driver);
        NumberTableColumn checkedColumn = getColumnAsNumber(getRowsWithWait(driver));
        assertThat(checkedColumn.getValue()).isEqualTo(String.valueOf(VALUE));
    }

    private static List<TableRow> getRowsWithWait(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> ViewTableActions.getRows(driver), rows -> !rows.isEmpty());
    }

    private static void createCustomTableWithNumberCell(WebDriver driver) {
        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.CUSTOM_TABLE);

        NewTableActions.fillTitle(driver, TITLE);
        NewTableActions.getTableHeads(driver)
            .getFirst()
            .setValue(TABLE_HEAD_CONTENT);

        NewTableActions.setColumnType(driver, 0, 0, ColumnType.NUMBER);

        NumberTableColumn tableColumn = getColumnAsNumber(NewTableActions.getRows(driver));
        tableColumn.setValue(VALUE);
        tableColumn.setStep(STEP);

        assertThat(tableColumn.getValue()).isEqualTo(String.valueOf(VALUE));
        assertThat(tableColumn.getStep()).isEqualTo(String.valueOf(STEP));

        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE))
            .assertTrue("Notebook page is not opened.");
    }

    private static NumberTableColumn getColumnAsNumber(List<TableRow> driver) {
        return driver.getFirst().getColumns().getFirst().as(ColumnType.NUMBER);
    }
}
