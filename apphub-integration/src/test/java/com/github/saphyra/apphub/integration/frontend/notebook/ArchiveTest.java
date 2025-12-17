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
import com.github.saphyra.apphub.integration.structure.view.notebook.CategoryTreeLeaf;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category";
    private static final String LIST_ITEM_TITLE = "list-item-title";

    @Test(groups = {"fe", "notebook"})
    public void archiveListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .pin(driver);

        //Archive item
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .archive(driver);

        assertThat(PinActions.findPinnedItemByTitle(driver, CATEGORY_TITLE).isArchived()).isTrue();

        CategoryTreeLeaf leaf = NotebookActions.getCategoryTree(driver);
        leaf = leaf.getChildren()
            .getFirst();
        assertThat(leaf.isArchived()).isTrue();

        //Unarchive item
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .unarchive(driver);

        assertThat(PinActions.findPinnedItemByTitle(driver, CATEGORY_TITLE).isArchived()).isFalse();

        leaf = NotebookActions.getCategoryTree(driver);
        leaf = leaf.getChildren()
            .getFirst();
        assertThat(leaf.isArchived()).isFalse();
    }

    @Test(groups = {"fe", "notebook"})
    public void contentOfArchivedCategoryShouldBeArchivedToo() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_TITLE);
        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_TITLE, CATEGORY_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .archive(driver)
            .open(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_TITLE).isArchived()).isTrue());
    }
}
