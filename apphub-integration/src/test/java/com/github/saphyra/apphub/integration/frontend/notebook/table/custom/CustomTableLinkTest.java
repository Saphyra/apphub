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
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableRow;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.LinkTableColumn;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableLinkTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final String URL = UserEndpoints.ACCOUNT_PAGE;
    private static final String NEW_URL = ModulesEndpoints.MODULES_PAGE;
    private static final String LABEL = "label";
    private static final String NEW_LABEL = "new-label";

    @Test(groups = {"fe", "notebook"})
    public void customTableLinkCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        blankLabel(driver);
        createCustomTableWithLinkCell(driver);
        openTable(driver);
        editColumn_blankLabel(driver);
        editColumn(driver);
    }

    private static void editColumn(WebDriver driver) {
        getColumnAsLink(ViewTableActions.getRows(driver))
            .setUrl(NEW_URL);

        getColumnAsLink(ViewTableActions.getRows(driver))
            .setLabel(NEW_LABEL);

        ViewTableActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ViewTableActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled");

        LinkTableColumn column = getColumnAsLink(ViewTableActions.getRows(driver));
        assertThat(column.getLabel()).isEqualTo(NEW_LABEL);

        column.open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(getServerPort(), ModulesEndpoints.MODULES_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }

    private void editColumn_blankLabel(WebDriver driver) {
        ViewTableActions.enableEditing(driver);
        getColumnAsLink(ViewTableActions.getRows(driver))
            .setLabel(" ");

        ViewTableActions.saveChanges(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_LINK_LABEL_BLANK);
    }

    private static void openTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open();
        LinkTableColumn column = getColumnAsLink(ViewTableActions.getRows(driver));
        assertThat(column.getLabel()).isEqualTo(LABEL);

        column.open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(getServerPort(), UserEndpoints.ACCOUNT_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }

    private static LinkTableColumn getColumnAsLink(List<TableRow> driver) {
        return driver.get(0)
            .getColumns()
            .get(0)
            .as(ColumnType.LINK);
    }

    private static void createCustomTableWithLinkCell(WebDriver driver) {
        LinkTableColumn tableColumn = getColumnAsLink(NewTableActions.getRows(driver));
        tableColumn.setLabel(LABEL);
        tableColumn.setUrl(URL);

        NewTableActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE))
            .assertTrue("Notebook page is not opened.");
    }

    private void blankLabel(WebDriver driver) {
        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.CUSTOM_TABLE);

        NewTableActions.fillTitle(driver, TITLE);
        NewTableActions.getTableHeads(driver)
            .get(0)
            .setValue(TABLE_HEAD_CONTENT);

        NewTableActions.setColumnType(driver, 0, 0, ColumnType.LINK);

        NewTableActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_LINK_LABEL_BLANK);
    }
}
