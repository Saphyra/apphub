package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchListItemTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = "url";
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

    @Test(groups = "notebook", priority = Integer.MIN_VALUE + 1)
    public void search() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);
        NotebookUtils.newLink(driver, LINK_TITLE, LINK_URL);
        NotebookUtils.newText(driver, TEXT_TITLE, TEXT_CONTENT);
        NotebookUtils.newChecklist(driver, CHECKLIST_TITLE, List.of(new BiWrapper<>(CHECKLIST_CONTENT, false)));
        NotebookUtils.newTable(driver, TABLE_TITLE, List.of(TABLE_HEAD), List.of(List.of(TABLE_CONTENT)));
        NotebookUtils.newChecklistTable(driver, CHECKLIST_TABLE_TITLE, List.of(CHECKLIST_TABLE_HEAD), List.of(new BiWrapper<>(false, List.of(CHECKLIST_TABLE_CONTENT))));
        NotebookUtils.newOnlyTitle(driver, ONLY_TITLE_TITLE);

        search(driver, CATEGORY_TITLE, CATEGORY_TITLE, ListItemType.CATEGORY);
        search(driver, LINK_TITLE, LINK_TITLE, ListItemType.LINK);
        search(driver, LINK_URL, LINK_TITLE, ListItemType.LINK);
        search(driver, TEXT_TITLE, TEXT_TITLE, ListItemType.TEXT);
        search(driver, TEXT_CONTENT, TEXT_TITLE, ListItemType.TEXT);
        search(driver, CHECKLIST_TITLE, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(driver, CHECKLIST_CONTENT, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(driver, TABLE_TITLE, TABLE_TITLE, ListItemType.TABLE);
        search(driver, TABLE_CONTENT, TABLE_TITLE, ListItemType.TABLE);
        search(driver, TABLE_HEAD, TABLE_TITLE, ListItemType.TABLE);
        search(driver, CHECKLIST_TABLE_TITLE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(driver, CHECKLIST_TABLE_CONTENT, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(driver, CHECKLIST_TABLE_HEAD, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(driver, ONLY_TITLE_TITLE, ONLY_TITLE_TITLE, ListItemType.ONLY_TITLE);
    }

    @Test(groups = "notebook")
    public void itemsShouldBeUnique() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newText(driver, TEXT_TITLE, TEXT_TITLE);

        search(driver, TEXT_TITLE, TEXT_TITLE, ListItemType.TEXT);
    }

    @Test(groups = "notebook")
    public void openParentOfSearchResult() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);
        NotebookUtils.newText(driver, TEXT_TITLE, TEXT_CONTENT, CATEGORY_TITLE);
        NotebookUtils.newChecklist(driver, CHECKLIST_TITLE, List.of(new BiWrapper<>(CHECKLIST_CONTENT, false)), CATEGORY_TITLE);

        NotebookActions.search(driver, TEXT_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, TEXT_TITLE)
            .openParent();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).size() == 2)
            .assertTrue("Parent is not opened.");

        assertThat(NotebookActions.getOpenedCategoryName(driver)).isEqualTo(CATEGORY_TITLE);
    }

    private void search(WebDriver driver, String searchText, String resultTitle, ListItemType resultType) {
        NotebookActions.search(driver, searchText);

        List<ListItem> listItems = NotebookActions.getListItems(driver);
        assertThat(listItems).hasSize(1);
        assertThat(listItems.get(0).getTitle()).isEqualTo(resultTitle);
        assertThat(listItems.get(0).getType()).isEqualTo(resultType);
    }
}
