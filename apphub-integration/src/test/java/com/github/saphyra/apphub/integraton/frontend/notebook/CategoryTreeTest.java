package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.DetailedListActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.CategoryTreeElement;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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

        CategoryActions.createCategory(driver, PARENT_TITLE);
        CategoryActions.createCategory(driver, CHILD_TITLE, PARENT_TITLE);

        CategoryTreeElement root = CategoryActions.getCategoryTreeRoot(driver);
        List<CategoryTreeElement> rootsChildren = root.getChildren();

        assertThat(rootsChildren).hasSize(1);
        CategoryTreeElement rootsChild = rootsChildren.get(0);
        rootsChild.openInMainWindow();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getTitleOfOpenedCategory(driver).equals(PARENT_TITLE))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).stream().anyMatch(listItemDetailsItem -> listItemDetailsItem.getTitle().equals(CHILD_TITLE)))
            .assertTrue();

        CategoryActions.getCategoryTreeRoot(driver)
            .getChildren()
            .get(0)
            .openChildrenList();

        List<CategoryTreeElement> youngestChildren = CategoryActions.getCategoryTreeRoot(driver)
            .getChildren()
            .get(0).getChildren();
        assertThat(youngestChildren).hasSize(1);
        CategoryTreeElement youngestChild = youngestChildren.get(0);
        youngestChild.openInMainWindow();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getTitleOfOpenedCategory(driver).equals(CHILD_TITLE))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).isEmpty())
            .assertTrue();
    }
}
