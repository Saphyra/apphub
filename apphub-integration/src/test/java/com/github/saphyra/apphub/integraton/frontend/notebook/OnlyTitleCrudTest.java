package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewOnlyTitleActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class OnlyTitleCrudTest extends SeleniumTest {
    private static final String ONLY_TITLE_TITLE = "only-title";
    private static final String NEW_ONLY_TITLE_TITLE = "new-only-title";

    @Test(groups = {"fe", "notebook"})
    public void onlyTitleCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.ONLY_TITLE);

        //Create - Blank title
        NewOnlyTitleActions.fillTitle(driver, " ");
        NewOnlyTitleActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Create
        NewOnlyTitleActions.fillTitle(driver, ONLY_TITLE_TITLE);
        NewOnlyTitleActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("OnlyTitle is not created");

        //Edit - Blank title
        NotebookActions.findListItemByTitleValidated(driver, ONLY_TITLE_TITLE)
            .edit(driver);
        EditListItemActions.fillTitle(driver, " ");

        EditListItemActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Edit
        EditListItemActions.fillTitle(driver, NEW_ONLY_TITLE_TITLE);

        EditListItemActions.submitForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Modifications are not saved.");

        //Delete
        NotebookActions.findListItemByTitleValidated(driver, NEW_ONLY_TITLE_TITLE)
            .deleteWithConfirmation(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, NEW_ONLY_TITLE_TITLE).isEmpty())
            .assertTrue("Link is not deleted.");
    }
}
