package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
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

    @Test(groups = {"fe", "notebook"})
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
        NewCategoryActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Create
        NewCategoryActions.fillTitle(driver, CATEGORY_1_TITLE);
        NewCategoryActions.submit(driver);

        NotebookUtils.waitForNotebookPageOpened(driver);

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

        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Edit
        EditListItemActions.fillTitle(driver, NEW_CATEGORY_TITLE);
        ParentSelectorActions.selectParent(driver, CATEGORY_2_TITLE);
        EditListItemActions.submitForm(driver);

        NotebookUtils.waitForNotebookPageOpened(driver);

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
        child.open();

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

        assertThat(NotebookActions.getCategoryTree(driver).hasChildren()).isFalse();
    }

    @Test(groups = {"fe", "notebook"})
    public void categoryTreeTest() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_1_TITLE);
        NotebookUtils.newCategory(driver, CATEGORY_2_TITLE, CATEGORY_1_TITLE);

        //Check initial state
        CategoryTreeLeaf root = NotebookActions.getCategoryTree(driver);
        assertThat(root.hasChildren()).isTrue();
        assertThat(root.isOpened()).isTrue();
        assertThat(root.getChildren()).hasSize(1);

        CategoryTreeLeaf child = root.getChildren()
            .get(0);
        assertThat(child.getTitle()).isEqualTo(CATEGORY_1_TITLE);
        assertThat(child.hasChildren()).isTrue();
        assertThat(child.isOpened()).isFalse();

        //Open child
        child.open();
        assertThat(child.isOpened()).isTrue();
        assertThat(child.getChildren()).hasSize(1);

        CategoryTreeLeaf grandchild = child.getChildren()
            .get(0);
        assertThat(grandchild.hasChildren()).isFalse();

        //Open categories
        grandchild.openCategory(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Category content is not loaded.");

        child.openCategory(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, CATEGORY_2_TITLE).isPresent())
            .assertTrue("Category content is not loaded.");

        root.openCategory(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, CATEGORY_1_TITLE).isPresent())
            .assertTrue("Category content is not loaded.");

        root.close();

        assertThat(root.isOpened()).isFalse();
    }
}
