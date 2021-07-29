package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ChecklistTableRow;
import com.github.saphyra.apphub.integration.frontend.model.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ViewChecklistItem;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.ChecklistTableActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.LinkActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.PinnedItemActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.TableActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.TextActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PinListItemTest extends SeleniumTest {
    private static final String ROOT_TITLE = "root-title";
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
    public void pinListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, ROOT_TITLE);
        LinkActions.createLink(driver, LINK_TITLE, LINK_URL);
        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);
        ChecklistActions.createChecklist(driver, CHECKLIST_TITLE, Arrays.asList(new NewChecklistItemData(CHECKLIST_ITEM_CONTENT, true)));
        TableActions.createTable(driver, TABLE_TITLE, Arrays.asList(TABLE_COLUMN_NAME), Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)));
        ChecklistTableActions.createChecklistTable(driver, CHECKLIST_TABLE_TITLE, Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME), Arrays.asList(new ChecklistTableRow(true, Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))));

        Stream.of(ROOT_TITLE, LINK_TITLE, TEXT_TITLE, CHECKLIST_TITLE, TABLE_TITLE, CHECKLIST_TABLE_TITLE)
            .forEach(title -> DetailedListActions.findDetailedItem(driver, title).pin(driver));

        PinnedItemActions.findPinnedItem(driver, LINK_TITLE)
            .open();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        assertThat(tabs).hasSize(2);
        driver.switchTo().window(tabs.get(1));
        assertThat(driver.getCurrentUrl()).endsWith(LINK_URL);
        driver.close();
        driver.switchTo().window(tabs.get(0));

        PinnedItemActions.findPinnedItem(driver, TEXT_TITLE)
            .open();
        AwaitilityWrapper.createDefault()
            .until(() -> TextActions.isViewTextWindowOpened(driver))
            .assertTrue("ViewText window not opened.");
        assertThat(TextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);

        PinnedItemActions.findPinnedItem(driver, CHECKLIST_TITLE)
            .open();
        AwaitilityWrapper.createDefault()
            .until(() -> ChecklistActions.isViewChecklistWindowOpened(driver))
            .assertTrue("ViewChecklist window not opened.");
        List<ViewChecklistItem> checklistItems = ChecklistActions.getChecklistItems(driver);
        assertThat(checklistItems).hasSize(1);
        assertThat(checklistItems.get(0).isChecked()).isTrue();
        assertThat(checklistItems.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_CONTENT);
        ChecklistActions.closeWindow(driver);

        PinnedItemActions.findPinnedItem(driver, TABLE_TITLE)
            .open();
        AwaitilityWrapper.createDefault()
            .until(() -> TableActions.isViewTableWindowOpened(driver))
            .assertTrue("ViewTable window not opened.");
        TableActions.verifyViewTable(driver, TABLE_TITLE, Arrays.asList(TABLE_COLUMN_NAME), Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)));

        PinnedItemActions.findPinnedItem(driver, CHECKLIST_TABLE_TITLE)
            .open();
        AwaitilityWrapper.createDefault()
            .until(() -> ChecklistTableActions.isViewChecklistTableWindowOpened(driver))
            .assertTrue("ViewChecklistTable window not opened.");
        ChecklistTableActions.verifyViewChecklistTable(driver, CHECKLIST_TABLE_TITLE, Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME), Arrays.asList(new ChecklistTableRow(true, Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))));

        PinnedItemActions.findPinnedItem(driver, ROOT_TITLE)
            .open();
        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.isDisplayed(driver))
            .assertTrue("Category not opened.");

        DetailedListActions.up(driver);

        PinnedItemActions.findPinnedItem(driver, LINK_TITLE)
            .unpin(driver);

        assertThat(DetailedListActions.findDetailedItem(driver, LINK_TITLE).isPinned()).isFalse();
    }
}
