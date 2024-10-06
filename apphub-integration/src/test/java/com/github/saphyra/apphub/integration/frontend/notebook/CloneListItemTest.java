package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTextActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.Link;
import com.github.saphyra.apphub.integration.structure.Number;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.TableRow;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.CheckedTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.LinkTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.NumberTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CloneListItemTest extends SeleniumTest {
    private static final String ROOT_TITLE = "root-category";
    private static final String CATEGORY_TITLE = "category-title";
    private static final String CHILD_CATEGORY_TITLE = "child-category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = ModulesEndpoints.MODULES_PAGE;
    private static final String TEXT_TITLE = "text-title";
    private static final String TEXT_CONTENT = "text-content";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String CHECKLIST_CONTENT = "checklist-content";
    private static final String TABLE_TITLE = "table-title0";
    private static final String TABLE_HEAD = "table-head0";
    private static final String TABLE_CONTENT = "table-content0";
    private static final String CHECKLIST_TABLE_CONTENT = "checklist-table-content";
    private static final String CHECKLIST_TABLE_TITLE = "checklist-table-title";
    private static final String ONLY_TITLE_TITLE = "only-title-title";
    private static final String CHECKLIST_TABLE_HEAD = "checklist-table-head";
    private static final String CUSTOM_TABLE_TITLE = "custom-table-title";
    private static final String CUSTOM_TABLE_COLUMN_NAME = "custom-table-column-name";
    private static final String CUSTOM_TABLE_LINK_LABEL = "custom-table-link-label";
    private static final Double CUSTOM_TABLE_NUMBER_VALUE = 4d;
    private static final Double CUSTOM_TABLE_NUMBER_STEP = 2d;
    private static final String CUSTOM_TABLE_TEXT = "custom-table-text";
    private static final List<String> LIST_ITEMS = List.of(
        CHILD_CATEGORY_TITLE,
        LINK_TITLE,
        TEXT_TITLE,
        CHECKLIST_TITLE,
        TABLE_TITLE,
        CHECKLIST_TABLE_TITLE,
        ONLY_TITLE_TITLE,
        CUSTOM_TABLE_TITLE
    );
    private static final String CUSTOM_TABLE_LINK_URL = UserEndpoints.ACCOUNT_PAGE;

    @Test(groups = {"fe", "notebook"})
    public void cloneListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, ROOT_TITLE);
        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_TITLE, ROOT_TITLE);
        NotebookActions.findListItemByTitleValidated(driver, ROOT_TITLE)
            .open(() -> NotebookActions.getOpenedCategoryName(driver).equals(ROOT_TITLE));
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open(() -> NotebookActions.getOpenedCategoryName(driver).equals(CATEGORY_TITLE));

        NotebookUtils.newCategory(getServerPort(), driver, CHILD_CATEGORY_TITLE);
        NotebookUtils.newLink(getServerPort(), driver, LINK_TITLE, LINK_URL);
        NotebookUtils.newText(getServerPort(), driver, TEXT_TITLE, TEXT_CONTENT);
        NotebookUtils.newChecklist(getServerPort(), driver, CHECKLIST_TITLE, List.of(new BiWrapper<>(CHECKLIST_CONTENT, true)));
        NotebookUtils.newTable(getServerPort(), driver, TABLE_TITLE, List.of(TABLE_HEAD), List.of(List.of(TABLE_CONTENT)));
        NotebookUtils.newChecklistTable(getServerPort(), driver, CHECKLIST_TABLE_TITLE, List.of(CHECKLIST_TABLE_HEAD), List.of(new BiWrapper<>(true, List.of(CHECKLIST_TABLE_CONTENT))));
        NotebookUtils.newOnlyTitle(getServerPort(), driver, ONLY_TITLE_TITLE);
        NotebookUtils.newCustomTable(
            getServerPort(),
            driver,
            CUSTOM_TABLE_TITLE,
            List.of(CUSTOM_TABLE_COLUMN_NAME),
            List.of(
                List.of(new BiWrapper<>(ColumnType.CHECKBOX, true)),
                List.of(new BiWrapper<>(ColumnType.LINK, new Link(CUSTOM_TABLE_LINK_LABEL, CUSTOM_TABLE_LINK_URL))),
                List.of(new BiWrapper<>(ColumnType.NUMBER, new Number(CUSTOM_TABLE_NUMBER_STEP, CUSTOM_TABLE_NUMBER_VALUE))),
                List.of(new BiWrapper<>(ColumnType.TEXT, CUSTOM_TABLE_TEXT))
            )
        );

        LIST_ITEMS.forEach(listItemTitle -> {
            NotebookActions.findListItemByTitleValidated(driver, listItemTitle).pin(driver);
            NotebookActions.findListItemByTitleValidated(driver, listItemTitle).archive(driver);
        });

        NotebookActions.up(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .cloneListItem();

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .edit(getServerPort(), driver);
        ParentSelectorActions.up(driver);
        EditListItemActions.submitForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE))
            .assertTrue("Category is not modified.");

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open();

        verifyContent(driver);

        NotebookActions.up(driver);
        NotebookActions.up(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open();

        verifyContent(driver);

        LIST_ITEMS.forEach(listItemTitle -> assertThat(PinActions.getPinnedItems(driver).stream().filter(listItem -> listItem.getTitle().equals(listItemTitle))).hasSize(2));
    }

    private void verifyContent(WebDriver driver) {
        verifyCategory(driver);
        verifyLink(driver);
        verifyText(driver);
        verifyChecklist(driver);
        verifyTable(driver);
        verifyChecklistTable(driver);
        verifyOnlyTitle(driver);
        verifyCustomTable(driver);

        LIST_ITEMS.forEach(listItemTitle -> assertThat(NotebookActions.findListItemByTitleValidated(driver, listItemTitle).isArchived()).isTrue());
    }

    private void verifyCustomTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, CUSTOM_TABLE_TITLE)
            .open();

        assertThat(ViewTableActions.getRows(driver)).hasSize(4);

        CheckedTableColumn checkedColumn = ViewTableActions.getRows(driver)
            .get(0)
            .getColumns()
            .get(0)
            .as(ColumnType.CHECKBOX);
        assertThat(checkedColumn.isChecked()).isTrue();

        LinkTableColumn linkColumn = ViewTableActions.getRows(driver)
            .get(1)
            .getColumns()
            .get(0)
            .as(ColumnType.LINK);
        linkColumn.open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(getServerPort(), UserEndpoints.ACCOUNT_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        NumberTableColumn numberColumn = ViewTableActions.getRows(driver)
            .get(2)
            .getColumns()
            .get(0)
            .as(ColumnType.NUMBER);
        assertThat(numberColumn.getValue()).isEqualTo(String.valueOf(CUSTOM_TABLE_NUMBER_VALUE.intValue()));

        assertThat(ViewTableActions.getRows(driver).get(3).getColumns().get(0).getValue()).isEqualTo(CUSTOM_TABLE_TEXT);

        ViewTableActions.close(driver);
    }

    private void verifyOnlyTitle(WebDriver driver) {
        assertThat(NotebookActions.findListItemByTitle(driver, ONLY_TITLE_TITLE)).isNotEmpty();
    }

    private void verifyChecklistTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TABLE_TITLE)
            .open();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(CHECKLIST_TABLE_HEAD);

        assertThat(ViewTableActions.getRows(driver)).hasSize(1);
        TableRow tableRow = ViewTableActions.getRows(driver)
            .get(0);
        assertThat(tableRow.isChecked()).isTrue();
        assertThat(tableRow.getColumns())
            .hasSize(1)
            .extracting(TableColumn::getValue)
            .containsExactly(CHECKLIST_TABLE_CONTENT);

        ViewTableActions.close(driver);
    }

    private void verifyTable(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .open();

        assertThat(ViewTableActions.getTableHeads(driver)).extracting(TableHead::getValue).containsExactly(TABLE_HEAD);
        assertThat(ViewTableActions.getRows(driver))
            .hasSize(1)
            .extracting(TableRow::getColumns)
            .hasSize(1)
            .extracting(tableColumns -> tableColumns.get(0).getValue())
            .containsExactly(TABLE_CONTENT);

        ViewTableActions.close(driver);
    }

    private void verifyChecklist(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TITLE)
            .open();

        ChecklistItem checklistItem = ViewChecklistActions.getItems(driver)
            .get(0);

        assertThat(checklistItem.getValue()).isEqualTo(CHECKLIST_CONTENT);
        assertThat(checklistItem.isChecked()).isTrue();

        ViewChecklistActions.close(driver);
    }

    private void verifyText(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, TEXT_TITLE)
            .open();

        assertThat(ViewTextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);

        ViewTextActions.close(driver);
    }

    private static void verifyLink(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .open();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(getServerPort(), ModulesEndpoints.MODULES_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }

    private static void verifyCategory(WebDriver driver) {
        assertThat(NotebookActions.findListItemByTitle(driver, CHILD_CATEGORY_TITLE)).isNotEmpty();
    }
}
