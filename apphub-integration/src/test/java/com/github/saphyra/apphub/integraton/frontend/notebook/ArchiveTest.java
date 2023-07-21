package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.CategoryTreeLeaf;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category";

    @Test(groups = "notebook")
    public void archiveListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);

        NotebookActions.findListItemByTitle(driver, CATEGORY_TITLE)
            .pin(driver);

        //Archive item
        NotebookActions.findListItemByTitle(driver, CATEGORY_TITLE)
            .archive(driver);

        assertThat(NotebookActions.findPinnedItemByTitle(driver, CATEGORY_TITLE).isArchived()).isTrue();

        CategoryTreeLeaf leaf = NotebookActions.getCategoryTree(driver);
        leaf = leaf.getChildren()
            .get(0);
        assertThat(leaf.isArchived()).isTrue();

        //Unarchive item
        NotebookActions.findListItemByTitle(driver, CATEGORY_TITLE)
            .unarchive(driver);

        assertThat(NotebookActions.findPinnedItemByTitle(driver, CATEGORY_TITLE).isArchived()).isFalse();

        leaf = NotebookActions.getCategoryTree(driver);
        leaf = leaf.getChildren()
            .get(0);
        assertThat(leaf.isArchived()).isFalse();
    }
}
