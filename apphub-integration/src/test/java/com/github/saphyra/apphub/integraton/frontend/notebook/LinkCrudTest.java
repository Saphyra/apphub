package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewLinkActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkCrudTest extends SeleniumTest {
    private static final String LINK_TITLE = "link";
    private static final String CATEGORY = "category";
    private static final String NEW_LINK_TITLE = "new-link";

    @Test(groups = {"fe", "notebook"})
    public void linkCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);
        NotebookUtils.newCategory(driver, CATEGORY);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.LINK);

        //Create - Blank title
        NewLinkActions.fillTitle(driver, "");
        NewLinkActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Create - Blank URL
        NewLinkActions.fillTitle(driver, LINK_TITLE);
        NewLinkActions.fillUrl(driver, " ");
        NewLinkActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, "URL must not be blank.");

        //Create
        NewLinkActions.fillUrl(driver, UrlFactory.create(Endpoints.MODULES_PAGE));
        ParentSelectorActions.selectParent(driver, CATEGORY);

        NewLinkActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Link is not created");

        //Open
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY)
            .open(() -> NotebookActions.findListItemByTitle(driver, LINK_TITLE).isPresent());

        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.MODULES_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        //Edit - Blank title
        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .edit(driver);
        EditListItemActions.fillTitle(driver, " ");

        EditListItemActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Title must not be blank.");

        //Edit - Blank URL
        EditListItemActions.fillTitle(driver, NEW_LINK_TITLE);
        EditListItemActions.fillValue(driver, " ");

        EditListItemActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, "URL must not be blank.");

        //Edit
        EditListItemActions.fillValue(driver, UrlFactory.create(Endpoints.ACCOUNT_PAGE));
        ParentSelectorActions.up(driver);

        EditListItemActions.submitForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Modifications are not saved.");

        //Open edited
        NotebookActions.up(driver);

        AwaitilityWrapper.findWithWait(() -> NotebookActions.getListItems(driver), listItem -> listItem.getTitle().equals(NEW_LINK_TITLE))
            .orElseThrow(() -> new RuntimeException("Modified Link not found"))
            .open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        //Delete
        NotebookActions.findListItemByTitleValidated(driver, NEW_LINK_TITLE)
            .deleteWithConfirmation(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, NEW_LINK_TITLE).isEmpty())
            .assertTrue("Link is not deleted.");
    }
}
