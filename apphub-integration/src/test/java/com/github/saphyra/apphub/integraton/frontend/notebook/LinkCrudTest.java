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
import com.github.saphyra.apphub.integration.localization.LocalizedText;
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

        create_blankTitle(driver);
        create_blankUrl(driver);
        create(driver);
        open(driver);
        edit_blankTitle(driver);
        edit_blankUrl(driver);
        edit(driver);
        openEdited(driver);
        delete(driver);
    }

    private static void create_blankTitle(WebDriver driver) {
        NewLinkActions.fillTitle(driver, "");
        NewLinkActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);
    }

    private static void create_blankUrl(WebDriver driver) {
        NewLinkActions.fillTitle(driver, LINK_TITLE);
        NewLinkActions.fillUrl(driver, " ");
        NewLinkActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_URL_MUST_NOT_BE_BLANK);
    }

    private static void create(WebDriver driver) {
        NewLinkActions.fillUrl(driver, UrlFactory.create(Endpoints.MODULES_PAGE));
        ParentSelectorActions.selectParent(driver, CATEGORY);

        NewLinkActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Link is not created");
    }

    private static void open(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY)
            .open(() -> NotebookActions.findListItemByTitle(driver, LINK_TITLE).isPresent());

        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.MODULES_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }

    private static void edit_blankTitle(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, LINK_TITLE)
            .edit(driver);
        EditListItemActions.fillTitle(driver, " ");

        EditListItemActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);
    }

    private static void edit_blankUrl(WebDriver driver) {
        EditListItemActions.fillTitle(driver, NEW_LINK_TITLE);
        EditListItemActions.fillValue(driver, " ");

        EditListItemActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_URL_MUST_NOT_BE_BLANK);
    }

    private static void edit(WebDriver driver) {
        EditListItemActions.fillValue(driver, UrlFactory.create(Endpoints.ACCOUNT_PAGE));
        ParentSelectorActions.up(driver);

        EditListItemActions.submitForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Modifications are not saved.");
    }

    private static void openEdited(WebDriver driver) {
        NotebookActions.up(driver);

        AwaitilityWrapper.findWithWait(() -> NotebookActions.getListItems(driver), listItem -> listItem.getTitle().equals(NEW_LINK_TITLE))
            .orElseThrow(() -> new RuntimeException("Modified Link not found"))
            .open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.ACCOUNT_PAGE));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }

    private static void delete(WebDriver driver) {
        NotebookActions.findListItemByTitleValidated(driver, NEW_LINK_TITLE)
            .deleteWithConfirmation(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, NEW_LINK_TITLE).isEmpty())
            .assertTrue("Link is not deleted.");
    }
}
