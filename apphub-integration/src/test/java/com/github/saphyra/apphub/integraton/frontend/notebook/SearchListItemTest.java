package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.ChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.ChecklistTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.LinkActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.NotebookPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.TableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.TextActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRow;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchListItemTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = Endpoints.ACCOUNT_PAGE;
    private static final String TEXT_CONTENT = "text-content";
    private static final String CHECKLIST_ITEM_CONTENT = "checklist-item-content";
    private static final String TABLE_COLUMN_NAME = "table-column-name";
    private static final String TABLE_COLUMN_VALUE = "table-column-value";
    private static final String TEXT_TITLE = "text-title";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String TABLE_TITLE = "table-title";
    private static final String CHECKLIST_TABLE_TITLE = "checklist-table-title";
    private static final String CHECKLIST_TABLE_COLUMN_NAME = "checklist-table-column-name";
    private static final String CHECKLIST_TABLE_COLUMN_VALUE = "checklist-table-column-value";

    @Test
    public void search() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE);
        LinkActions.createLink(driver, LINK_TITLE, LINK_URL, CATEGORY_TITLE);
        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);
        ChecklistActions.createChecklist(driver, CHECKLIST_TITLE, Arrays.asList(new NewChecklistItemData(CHECKLIST_ITEM_CONTENT, true)));
        TableActions.createTable(driver, TABLE_TITLE, Arrays.asList(TABLE_COLUMN_NAME), Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)));
        ChecklistTableActions.createChecklistTable(driver, CHECKLIST_TABLE_TITLE, Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME), Arrays.asList(new ChecklistTableRow(true, Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))));

        search(driver, CATEGORY_TITLE, CATEGORY_TITLE, ListItemType.CATEGORY);
        search(driver, LINK_TITLE, LINK_TITLE, ListItemType.LINK);
        search(driver, LINK_URL, LINK_TITLE, ListItemType.LINK);
        search(driver, TEXT_TITLE, TEXT_TITLE, ListItemType.TEXT);
        search(driver, TEXT_CONTENT, TEXT_TITLE, ListItemType.TEXT);
        search(driver, CHECKLIST_TITLE, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(driver, CHECKLIST_ITEM_CONTENT, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(driver, TABLE_TITLE, TABLE_TITLE, ListItemType.TABLE);
        search(driver, TABLE_COLUMN_NAME, TABLE_TITLE, ListItemType.TABLE);
        search(driver, TABLE_COLUMN_VALUE, TABLE_TITLE, ListItemType.TABLE);
        search(driver, CHECKLIST_TABLE_TITLE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(driver, CHECKLIST_TABLE_COLUMN_NAME, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(driver, CHECKLIST_TABLE_COLUMN_VALUE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
    }

    @Test
    public void sameItemShouldBeReturnedOnlyOnce() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        LinkActions.createLink(driver, LINK_TITLE, LINK_TITLE);
        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);

        NotebookPageActions.search(driver, LINK_TITLE);
        SleepUtil.sleep(1500);

        assertThat(DetailedListActions.getDetailedListItems(driver)).hasSize(1);
    }

    @Test
    public void openCategoryOfSearchedItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE);
        LinkActions.createLink(driver, LINK_TITLE, LINK_TITLE, CATEGORY_TITLE);
        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT, CATEGORY_TITLE);

        NotebookPageActions.search(driver, LINK_TITLE);
        SleepUtil.sleep(1500);

        assertThat(DetailedListActions.getDetailedListItems(driver)).hasSize(1);
        DetailedListActions.getDetailedListItems(driver)
            .get(0)
            .openParentCategory();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).size() == 2)
            .assertTrue("Parent not loaded");
    }

    private void search(WebDriver driver, String searchText, String listItemTitle, ListItemType type) {
        NotebookPageActions.search(driver, searchText);
        SleepUtil.sleep(1500);

        ListItemDetailsItem searchResult = DetailedListActions.getDetailedListItems(driver).stream().filter(item -> item.getTitle().equals(listItemTitle)).findFirst()
            .orElseThrow(() -> new RuntimeException("ListItem not found for searchText " + searchText + " with title " + listItemTitle));

        assertThat(searchResult.getType()).isEqualTo(type);
    }
}
