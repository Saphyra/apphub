package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.notebook.CategoryTreeElement;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.NotebookPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTreeTest extends SeleniumTest {
    private static final String PARENT_TITLE = "parent-title";
    private static final String CHILD_TITLE = "child-title";

    @Test
    public void openCategoryFromTree() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookPageActions.createCategory(driver, PARENT_TITLE);
        NotebookPageActions.createCategory(driver, CHILD_TITLE, PARENT_TITLE);

        CategoryTreeElement root = NotebookPageActions.getCategoryTreeRoot(driver);
        List<CategoryTreeElement> rootsChildren = root.getChildren();

        assertThat(rootsChildren).hasSize(1);
        CategoryTreeElement rootsChild = rootsChildren.get(0);
        rootsChild.openInMainWindow();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPageActions.getTitleOfOpenedCategory(driver).equals(PARENT_TITLE))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPageActions.getDetailedListItems(driver).stream().anyMatch(listItemDetailsItem -> listItemDetailsItem.getTitle().equals(CHILD_TITLE)))
            .assertTrue();

        rootsChild.openChildrenList();
        List<CategoryTreeElement> youngestChildren = rootsChild.getChildren();
        assertThat(youngestChildren).hasSize(1);
        CategoryTreeElement youngestChild = youngestChildren.get(0);
        youngestChild.openInMainWindow();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPageActions.getTitleOfOpenedCategory(driver).equals(CHILD_TITLE))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPageActions.getDetailedListItems(driver).isEmpty())
            .assertTrue();
    }
}
