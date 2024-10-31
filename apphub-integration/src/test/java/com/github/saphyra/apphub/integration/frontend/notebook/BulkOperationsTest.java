package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BulkOperationsTest extends SeleniumTest {
    private static final String LIST_ITEM_1 = "list-item-1";
    private static final String LIST_ITEM_2 = "list-item-2";
    private static final String LIST_ITEM_3 = "list-item-3";

    @Test(groups = {"fe", "notebook"})
    public void bulkOperations() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_1);
        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_2);

        //Toggle selections
        NotebookActions.selectAll(driver);

        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1).isSelected()).isTrue();
        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2).isSelected()).isTrue();

        NotebookActions.unselectAll(driver);

        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1).isSelected()).isFalse();
        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2).isSelected()).isFalse();

        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1)
            .toggleSelect();

        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1).isSelected()).isTrue();
        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2).isSelected()).isFalse();

        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1)
            .toggleSelect();

        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1).isSelected()).isFalse();
        assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2).isSelected()).isFalse();

        //Archive
        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_3);

        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1)
            .toggleSelect();
        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2)
            .toggleSelect();

        NotebookActions.archiveSelected(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1).isArchived()).isTrue();
            assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2).isArchived()).isTrue();
            assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_3).isArchived()).isFalse();
        });

        NotebookActions.unarchiveSelected(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_1).isArchived()).isFalse();
            assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_2).isArchived()).isFalse();
            assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_3).isArchived()).isFalse();
        });

        //Pin
        NotebookActions.pinSelected(driver);
        AwaitilityWrapper.awaitAssert(() -> PinActions.getPinnedItems(driver), listItems -> assertThat(listItems).extracting(ListItem::getTitle).containsExactlyInAnyOrder(LIST_ITEM_1, LIST_ITEM_2));

        NotebookActions.unpinSelected(driver);
        AwaitilityWrapper.awaitAssert(() -> PinActions.getPinnedItems(driver), listItems -> assertThat(listItems).isEmpty());

        //Delete
        NotebookActions.deleteSelected(driver);
        AwaitilityWrapper.awaitAssert(() -> NotebookActions.getListItems(driver), listItems -> assertThat(listItems).extracting(ListItem::getTitle).containsExactly(LIST_ITEM_3));
    }
}
