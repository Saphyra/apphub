package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewCategoryActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.CategoryTreeLeaf;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryCrudTest extends SeleniumTest {
    private static final String CATEGORY_1_TITLE = "category-1";
    private static final String CATEGORY_2_TITLE = "category-2";
    private static final String NEW_CATEGORY_TITLE = "new-category-title";

    @Test(groups = "notebook")
    public void categoryCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.CATEGORY);

        //Create - Blank title
        NewCategoryActions.fillTitle(driver, " ");
        NewCategoryActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Cím nem lehet üres.");

        //Create
        NewCategoryActions.create(driver, CATEGORY_1_TITLE);

        assertThat(NotebookActions.getListItems(driver))
            .hasSize(1)
            .extracting(ListItem::getTitle)
            .containsExactly(CATEGORY_1_TITLE);

        assertThat(NotebookActions.getCategoryTree(driver).getChildren()).extracting(CategoryTreeLeaf::getTitle).containsExactly(CATEGORY_1_TITLE);

        //Edit - Empty title
        NotebookUtils.newCategory(driver, CATEGORY_2_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_1_TITLE)
            .edit(driver);

        EditListItemActions.fillTitle(driver, " ");
        EditListItemActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Cím nem lehet üres.");

        //Edit
        EditListItemActions.fillTitle(driver, NEW_CATEGORY_TITLE);
        EditListItemActions.selectParent(driver, CATEGORY_2_TITLE);
        EditListItemActions.submitForm(driver);

        NotebookActions.waitForPageOpened(driver);

        List<ListItem> listItems = NotebookActions.getListItems(driver);
        assertThat(listItems).hasSize(1);
        listItems.get(0)
            .open();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, NEW_CATEGORY_TITLE).isPresent())
            .assertTrue("Category not opened");

        CategoryTreeLeaf leaf = NotebookActions.getCategoryTree(driver);
        assertThat(leaf.getChildren()).extracting(CategoryTreeLeaf::getTitle).containsExactly(CATEGORY_2_TITLE);
        CategoryTreeLeaf child = leaf.getChildren()
            .get(0);
        child.expand();

        assertThat(child.getChildren()).hasSize(1)
            .extracting(CategoryTreeLeaf::getTitle)
            .containsExactly(NEW_CATEGORY_TITLE);

        //Delete category
        NotebookActions.up(driver);

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, CATEGORY_2_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Category not found with name " + CATEGORY_2_TITLE))
            .delete(driver);

        NotebookActions.cancelListItemDeletion(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_2_TITLE)
            .delete(driver);

        NotebookActions.confirmListItemDeletion(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("ListItem is not deleted.");

        assertThat(NotebookActions.getCategoryTree(driver).getChildren()).isEmpty();
    }
}
