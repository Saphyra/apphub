package com.github.saphyra.apphub.integration.frontend.notebook.table;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableRow;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AddTableRowTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String FIRST_COLUMN = "first-column";

    @Test(groups = {"fe", "notebook"})
    public void addRowsToTable() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.CHECKLIST_TABLE);
        NewTableActions.fillTitle(driver, TITLE);
        NewTableActions.getTableHeads(driver)
            .getFirst()
            .setValue(COLUMN_NAME);
        NewTableActions.getRows(driver)
            .getFirst()
            .getColumns()
            .getFirst()
            .setValue(COLUMN_VALUE);
        NewTableActions.submit(driver);


        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Table not found."))
            .open(driver);

        ViewTableActions.enableEditing(driver);

        addRowToStart(driver);
        addRowToEnd(driver);
    }

    private static void addRowToStart(WebDriver driver) {
        ViewTableActions.addRowToStart(driver);

        List<TableRow> rows = AwaitilityWrapper.getListWithWait(() -> ViewTableActions.getRows(driver), list -> list.size() == 2);
        assertThat(rows).hasSize(2);
        TableRow row = rows.getFirst();
        assertThat(row.isChecked()).isFalse();
        TableColumn column = row.getColumns()
            .getFirst();
        assertThat(column.getValue()).isEmpty();
        column.setValue(FIRST_COLUMN);
    }

    private void addRowToEnd(WebDriver driver) {
        ViewTableActions.addRowToEnd(driver);

        List<TableRow> rows = ViewTableActions.getRows(driver);
        assertThat(rows).hasSize(3);
        TableRow row = rows.get(2);
        assertThat(row.isChecked()).isFalse();
        TableColumn column = row.getColumns()
            .getFirst();
        assertThat(column.getValue()).isEmpty();
    }
}
