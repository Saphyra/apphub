package com.github.saphyra.apphub.integration.frontend.notebook.checklist;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewChecklistActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AddChecklistItemTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String LAST_CONTENT = "last-content";
    private static final String FIRST_CONTENT = "first-content";

    @Test(groups = {"fe", "notebook"})
    public void addChecklistItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookActions.newListItem(getServerPort(), driver);
        NotebookNewListItemActions.selectListItemType(getServerPort(), driver, ListItemType.CHECKLIST);
        NewChecklistActions.fillTitle(driver, TITLE);
        NewChecklistActions.getItems(driver)
            .get(0)
            .setValue(CONTENT);
        NewChecklistActions.submit(driver);

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("List item not found."))
            .open();

        ViewChecklistActions.addItem(driver);
        ViewChecklistActions.setNewChecklistItemContent(driver, LAST_CONTENT);
        ViewChecklistActions.cancelAddItem(driver);

        List<ChecklistItem> items = AwaitilityWrapper.getListWithWait(() -> ViewChecklistActions.getItems(driver), checklistItems -> checklistItems.size() == 2);
        assertThat(items).hasSize(2);
        assertThat(items.get(1).getValue()).isEqualTo(LAST_CONTENT);

        ViewChecklistActions.addItemToTheStart(driver);
        ViewChecklistActions.setNewChecklistItemContent(driver, FIRST_CONTENT);
        ViewChecklistActions.cancelAddItem(driver);

        items = AwaitilityWrapper.getListWithWait(() -> ViewChecklistActions.getItems(driver), checklistItems -> checklistItems.size() == 3);
        assertThat(items).hasSize(3);
        assertThat(items.get(0).getValue()).isEqualTo(FIRST_CONTENT);
    }
}
