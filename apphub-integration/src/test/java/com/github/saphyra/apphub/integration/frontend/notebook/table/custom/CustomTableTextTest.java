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
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableTextTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final String TEXT = "text";
    private static final String NEW_TEXT = "new-text";

    @Test(groups = {"fe", "notebook"})
    public void customTableTextCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        createCustomTableWithTextCell(driver);
        openTable(driver);
        editColumn(driver);
    }

    private static void editColumn(WebDriver driver) {
        ViewTableActions.enableEditing(driver);
        getColumnAsLink(ViewTableActions.getRows(driver))
            .setValue(NEW_TEXT);
        assertThat(getColumnAsLink(ViewTableActions.getRows(driver)).getValue()).isEqualTo(NEW_TEXT);

        ViewTableActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ViewTableActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled");

        assertThat(getColumnAsLink(ViewTableActions.getRows(driver)).getValue()).isEqualTo(NEW_TEXT);
    }

    private static void openTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open();
        TableColumn tableColumn = getColumnAsLink(NewTableActions.getRows(driver));
        assertThat(tableColumn.getValue()).isEqualTo(TEXT);
    }

    private static TableColumn getColumnAsLink(List<TableRow> driver) {
        return driver.get(0).getColumns().get(0);
    }

    private static void createCustomTableWithTextCell(WebDriver driver) {
        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.CUSTOM_TABLE);

        NewTableActions.fillTitle(driver, TITLE);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_CONTENT);

        NewTableActions.setColumnType(driver, 0, 0, ColumnType.TEXT);

        TableColumn tableColumn = getColumnAsLink(NewTableActions.getRows(driver));
        tableColumn.setValue(TEXT);

        assertThat(tableColumn.getValue()).isEqualTo(TEXT);

        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE))
            .assertTrue("Notebook page is not opened.");
    }
}
