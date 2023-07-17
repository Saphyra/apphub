package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.NotebookPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.OnlyTitleActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OnlyTitleCrudTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String CATEGORY_TITLE = "category-title";
    private static final String NEW_TITLE = "new-title";

    @Test
    public void onlyTitleCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        //Create - Empty title
        OnlyTitleActions.openCreateOnlyTitleWindow(driver);
        OnlyTitleActions.submitCreateOnlyTitleForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(OnlyTitleActions.isCreateOnlyTitleWindowDisplayed(driver)).isTrue();

        //Create
        OnlyTitleActions.fillNewOnlyTitleTitle(driver, TITLE);
        OnlyTitleActions.submitCreateOnlyTitleForm(driver);
        NotificationUtil.verifySuccessNotification(driver, "Sikeresen létrehozva.");
        assertThat(OnlyTitleActions.isCreateOnlyTitleWindowDisplayed(driver)).isFalse();
        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).stream().anyMatch(listItemDetailsItem -> listItemDetailsItem.getTitle().equals(TITLE)))
            .assertTrue();

        //Edit - Empty title
        ListItemDetailsItem detailsItem = DetailedListActions.findDetailedItem(driver, TITLE);
        detailsItem.edit(driver);
        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        DetailedListActions.closeEditListItemWindow(driver);

        NotificationUtil.clearNotifications(driver);

        //Edit
        CategoryActions.createCategory(driver, CATEGORY_TITLE);
        detailsItem = DetailedListActions.findDetailedItem(driver, TITLE);
        detailsItem.edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, NEW_TITLE, null, 0, CATEGORY_TITLE);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);

        AwaitilityWrapper.getListWithWait(() -> DetailedListActions.getDetailedListItems(driver), items -> items.size() == 1)
            .get(0)
            .open();

        detailsItem = DetailedListActions.findDetailedItem(driver, NEW_TITLE);

        //Delete
        detailsItem.delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).isEmpty())
            .assertTrue("OnlyTitle is not deleted.");
    }
}
