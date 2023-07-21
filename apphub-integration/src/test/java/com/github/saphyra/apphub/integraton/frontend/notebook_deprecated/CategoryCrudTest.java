package com.github.saphyra.apphub.integraton.frontend.notebook_deprecated;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.NotebookPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeElement;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryCrudTest extends SeleniumTest {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final String TITLE_3 = "title-3";
    private static final String TITLE_4 = "title-4";

    @Test
    public void categoryCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        //Create - Empty title
        CategoryActions.openCreateCategoryWindow(driver);
        CategoryActions.submitCreateCategoryForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(CategoryActions.isCreateCategoryWindowDisplayed(driver)).isTrue();

        //Create
        CategoryActions.fillNewCategoryTitle(driver, TITLE_1);
        CategoryActions.submitCreateCategoryForm(driver);
        NotificationUtil.verifySuccessNotification(driver, "Kategória elmentve.");
        assertThat(CategoryActions.isCreateCategoryWindowDisplayed(driver)).isFalse();
        AwaitilityWrapper.createDefault()
            .until(() -> CategoryActions.getCategoryTreeRoot(driver).getChildren().stream().anyMatch(categoryTreeElement -> categoryTreeElement.getTitle().equals(TITLE_1)))
            .assertTrue();
        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).stream().anyMatch(listItemDetailsItem -> listItemDetailsItem.getTitle().equals(TITLE_1)))
            .assertTrue();

        //Edit - Empty title
        ListItemDetailsItem detailsItem = DetailedListActions.findDetailedItem(driver, TITLE_1);
        detailsItem.edit(driver);
        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        DetailedListActions.closeEditListItemWindow(driver);

        NotificationUtil.clearNotifications(driver);
        //Edit
        CategoryActions.createCategory(driver, TITLE_2);
        CategoryActions.createCategory(driver, TITLE_3, TITLE_1);

        CategoryActions.openCategory(driver, TITLE_1);
        detailsItem = DetailedListActions.findDetailedItem(driver, TITLE_3);
        detailsItem.edit(driver);
        NotebookPageActions.fillEditListItemDialog(driver, TITLE_4, null, 1, TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
        DetailedListActions.up(driver);
        CategoryActions.openCategory(driver, TITLE_2);
        DetailedListActions.findDetailedItem(driver, TITLE_4);
    }

    @Test
    public void deleteCategory() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, TITLE_1);
        CategoryActions.createCategory(driver, TITLE_2, TITLE_1);
        CategoryActions.createCategory(driver, TITLE_3, TITLE_1, TITLE_2);

        CategoryActions.openCategory(driver, TITLE_1);
        CategoryActions.deleteCategory(driver, TITLE_2);

        NotificationUtil.verifySuccessNotification(driver, "Kategória törölve.");

        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();

        CategoryTreeElement root = CategoryActions.getCategoryTreeRoot(driver);
        assertThat(root.getChildren()).hasSize(1);
        assertThat(root.getChildren().get(0).getChildren()).isEmpty();
    }

    @Test
    public void editCategory() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, TITLE_1);
        CategoryActions.createCategory(driver, TITLE_2);
        CategoryActions.createCategory(driver, TITLE_3, TITLE_1);

        CategoryActions.openCategory(driver, TITLE_1);

        ListItemDetailsItem detailsItem = DetailedListActions.findDetailedItem(driver, TITLE_3);
        detailsItem.edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, TITLE_4, null, 1, TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);

        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();

        DetailedListActions.up(driver);
        CategoryActions.openCategory(driver, TITLE_2);

        DetailedListActions.findDetailedItem(driver, TITLE_4);
    }
}
