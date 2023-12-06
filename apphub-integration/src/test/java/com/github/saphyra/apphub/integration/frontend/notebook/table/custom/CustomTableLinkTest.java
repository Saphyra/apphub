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
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableLinkTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final String LINK = "link";
    private static final String NEW_LINK = "new-link";

    @Test(groups = {"fe", "notebook"})
    public void customTableLinkCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        createCustomTableWithLinkCell(driver);
        openTable(driver);
        editColumn(driver);
    }

    private static void editColumn(WebDriver driver) {
        ViewTableActions.enableEditing(driver);
        getColumnAsLink(ViewTableActions.getRows(driver))
            .setValue(NEW_LINK);
        assertThat(getColumnAsLink(ViewTableActions.getRows(driver)).getValue()).isEqualTo(NEW_LINK);

        ViewTableActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ViewTableActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled");

        assertThat(getColumnAsLink(ViewTableActions.getRows(driver)).getValue()).isEqualTo(NEW_LINK);
    }

    private static void openTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open();
        TableColumn tableColumn = getColumnAsLink(NewTableActions.getRows(driver));
        assertThat(tableColumn.getValue()).isEqualTo(LINK);
    }

    private static TableColumn getColumnAsLink(List<TableRow> driver) {
        return driver.get(0).getColumns().get(0);
    }

    private static void createCustomTableWithLinkCell(WebDriver driver) {
        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItemType(driver, ListItemType.CUSTOM_TABLE);

        NewTableActions.fillTitle(driver, TITLE);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_CONTENT);

        NewTableActions.setColumnType(driver, 0, 0, ColumnType.LINK);

        TableColumn tableColumn = getColumnAsLink(NewTableActions.getRows(driver));
        tableColumn.setValue(LINK);

        assertThat(tableColumn.getValue()).isEqualTo(LINK);

        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Notebook page is not opened.");
    }
}
