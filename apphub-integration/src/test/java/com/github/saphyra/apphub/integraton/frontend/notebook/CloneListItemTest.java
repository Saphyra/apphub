package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.OnlyTitleActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ChecklistTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.TextActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRow;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.structure.notebook.ViewChecklistItem;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CloneListItemTest extends SeleniumTest {
    private static final String ROOT_TITLE = "root-title";
    private static final String PARENT_TITLE = "parent-title";
    private static final String CHILD_CATEGORY_TITLE = "child-category-title";
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
    private static final String ONLY_TITLE_TITLE = "only-title-title";

    @Test(priority = -1)
    public void cloneListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, ROOT_TITLE);
        CategoryActions.createCategory(driver, PARENT_TITLE, ROOT_TITLE);
        CategoryActions.createCategory(driver, CHILD_CATEGORY_TITLE, ROOT_TITLE, PARENT_TITLE);
        LinkActions.createLink(driver, LINK_TITLE, LINK_URL, ROOT_TITLE, PARENT_TITLE, CHILD_CATEGORY_TITLE);
        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT, ROOT_TITLE, PARENT_TITLE);
        OnlyTitleActions.createOnlyTitle(driver, ONLY_TITLE_TITLE, ROOT_TITLE, PARENT_TITLE);
        ChecklistActions.createChecklist(driver, CHECKLIST_TITLE, Arrays.asList(new NewChecklistItemData(CHECKLIST_ITEM_CONTENT, true)), ROOT_TITLE, PARENT_TITLE);
        TableActions.createTable(driver, TABLE_TITLE, Arrays.asList(TABLE_COLUMN_NAME), Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)), ROOT_TITLE, PARENT_TITLE);
        ChecklistTableActions.createChecklistTable(driver, CHECKLIST_TABLE_TITLE, Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME), Arrays.asList(new ChecklistTableRow(true, Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))), ROOT_TITLE, PARENT_TITLE);

        DetailedListActions.findDetailedItem(driver, ROOT_TITLE)
            .open();

        ListItemDetailsItem parentItem = DetailedListActions.findDetailedItem(driver, PARENT_TITLE);
        String parentId = parentItem.getId();
        parentItem.cloneItem(driver);

        NotificationUtil.verifySuccessNotification(driver, "Duplikálás sikeres.");

        ListItemDetailsItem clonedParentItem = AwaitilityWrapper.getListWithWait(
            () -> DetailedListActions.getDetailedListItems(driver),
            listItemDetailsItems -> listItemDetailsItems.size() == 2
        ).stream()
            .filter(listItemDetailsItem -> !listItemDetailsItem.getId().equals(parentId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Clone not found"));
        clonedParentItem.open();

        DetailedListActions.findDetailedItem(driver, CHILD_CATEGORY_TITLE)
            .open();

        DetailedListActions.findDetailedItem(driver, LINK_TITLE)
            .open();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        assertThat(tabs).hasSize(2);
        driver.switchTo().window(tabs.get(1));
        assertThat(driver.getCurrentUrl()).endsWith(LINK_URL);
        driver.close();
        driver.switchTo().window(tabs.get(0));

        DetailedListActions.up(driver);

        DetailedListActions.findDetailedItem(driver, ONLY_TITLE_TITLE);

        TextActions.openText(driver, TEXT_TITLE);
        assertThat(TextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);
        TextActions.closeView(driver);

        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        List<ViewChecklistItem> checklistItems = ChecklistActions.getChecklistItems(driver);
        assertThat(checklistItems).hasSize(1);
        assertThat(checklistItems.get(0).isChecked()).isTrue();
        assertThat(checklistItems.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_CONTENT);
        ChecklistActions.closeWindow(driver);

        TableActions.openTable(driver, TABLE_TITLE);
        TableActions.verifyViewTable(driver, TABLE_TITLE, Arrays.asList(TABLE_COLUMN_NAME), Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)));
        TableActions.closeViewTableWindow(driver);

        ChecklistTableActions.openChecklistTable(driver, CHECKLIST_TABLE_TITLE);
        ChecklistTableActions.verifyViewChecklistTable(driver, CHECKLIST_TABLE_TITLE, Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME), Arrays.asList(new ChecklistTableRow(true, Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))));
    }
}
