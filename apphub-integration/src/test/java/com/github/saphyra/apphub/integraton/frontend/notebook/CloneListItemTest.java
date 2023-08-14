package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTextActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableHead;
import com.github.saphyra.apphub.integration.structure.view.notebook.TableRow;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CloneListItemTest extends SeleniumTest {
    private static final String ROOT_TITLE = "root-category";
    private static final String CATEGORY_TITLE = "category-title";
    private static final String CHILD_CATEGORY_TITLE = "child-category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = Endpoints.MODULES_PAGE;
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

    @Test(groups = {"fe", "notebook"}, priority = Integer.MIN_VALUE)
    public void cloneListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, ROOT_TITLE);
        NotebookUtils.newCategory(driver, CATEGORY_TITLE, ROOT_TITLE);
        NotebookActions.findListItemByTitleValidated(driver, ROOT_TITLE)
            .open(() -> NotebookActions.getOpenedCategoryName(driver).equals(ROOT_TITLE));
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open(() -> NotebookActions.getOpenedCategoryName(driver).equals(CATEGORY_TITLE));

        NotebookUtils.newCategory(driver, CHILD_CATEGORY_TITLE);
        NotebookUtils.newLink(driver, LINK_TITLE, LINK_URL);
        NotebookUtils.newText(driver, TEXT_TITLE, TEXT_CONTENT);
        NotebookUtils.newChecklist(driver, CHECKLIST_TITLE, List.of(new BiWrapper<>(CHECKLIST_CONTENT, true)));
        NotebookUtils.newTable(driver, TABLE_TITLE, List.of(TABLE_HEAD), List.of(List.of(TABLE_CONTENT)));
        NotebookUtils.newChecklistTable(driver, CHECKLIST_TABLE_TITLE, List.of(CHECKLIST_TABLE_HEAD), List.of(new BiWrapper<>(true, List.of(CHECKLIST_TABLE_CONTENT))));
        NotebookUtils.newOnlyTitle(driver, ONLY_TITLE_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, CHILD_CATEGORY_TITLE)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, TEXT_TITLE)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TITLE)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TABLE_TITLE)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, ONLY_TITLE_TITLE)
            .archive(driver);

        NotebookActions.findListItemByTitleValidated(driver, CHILD_CATEGORY_TITLE)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, TEXT_TITLE)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TITLE)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, TABLE_TITLE)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TABLE_TITLE)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, ONLY_TITLE_TITLE)
            .pin(driver);

        NotebookActions.up(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .cloneListItem();

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .edit(driver);
        ParentSelectorActions.up(driver);
        EditListItemActions.submitForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Category is not modified.");

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open();

        verifyContent(driver);

        NotebookActions.up(driver);
        NotebookActions.up(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open();

        verifyContent(driver);

        verifyPinnedItems(driver);
    }

    private void verifyPinnedItems(WebDriver driver) {
        Stream.of(
            CHILD_CATEGORY_TITLE,
            LINK_TITLE,
            TEXT_TITLE,
            CHECKLIST_TITLE,
            TABLE_TITLE,
            CHECKLIST_TABLE_TITLE,
            ONLY_TITLE_TITLE
        ).forEach(listItemTitle -> assertThat(NotebookActions.getPinnedItems(driver).stream().filter(listItem -> listItem.getTitle().equals(listItemTitle))).hasSize(2));
    }

    private void verifyContent(WebDriver driver) {
        verifyCategory(driver);
        verifyLink(driver);
        verifyText(driver);
        verifyChecklist(driver);
        verifyTable(driver);
        verifyChecklistTable(driver);
        verifyOnlyTitle(driver);

        Stream.of(
            CHILD_CATEGORY_TITLE,
            LINK_TITLE,
            TEXT_TITLE,
            CHECKLIST_TITLE,
            TABLE_TITLE,
            CHECKLIST_TABLE_TITLE,
            ONLY_TITLE_TITLE
        ).forEach(listItemTitle -> assertThat(NotebookActions.findListItemByTitleValidated(driver, listItemTitle).isArchived()).isTrue());
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
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.MODULES_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }

    private static void verifyCategory(WebDriver driver) {
        assertThat(NotebookActions.findListItemByTitle(driver, CHILD_CATEGORY_TITLE)).isNotEmpty();
    }
}
