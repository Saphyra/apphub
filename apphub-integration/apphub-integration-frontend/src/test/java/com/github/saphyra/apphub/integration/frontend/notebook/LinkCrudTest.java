package com.github.saphyra.apphub.integration.frontend.notebook;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.LinkActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.NotebookPageActions;

public class LinkCrudTest extends SeleniumTest {
    private static final String URL = Endpoints.ACCOUNT_PAGE;
    private static final String TITLE = "title";
    private static final String CATEGORY_TITLE_1 = "category-title-1";
    private static final String CATEGORY_TITLE_2 = "category-title-2";
    private static final String NEW_URL = Endpoints.NOTEBOOK_PAGE;
    private static final String NEW_TITLE = "new-title";

    @Test
    public void createLink_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        LinkActions.openCreateLinkWindow(driver);
        LinkActions.fillCreateLinkForm(driver, "", URL);
        LinkActions.submitCreateLinkForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(LinkActions.isCreateLinkWindowDisplayed(driver)).isTrue();
    }

    @Test
    public void createLink_emptyUrl() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        LinkActions.openCreateLinkWindow(driver);
        LinkActions.fillCreateLinkForm(driver, TITLE, "");
        LinkActions.submitCreateLinkForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "URL nem lehet üres.");
        assertThat(LinkActions.isCreateLinkWindowDisplayed(driver)).isTrue();
    }

    @Test
    public void createLink() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);

        LinkActions.openCreateLinkWindow(driver);
        LinkActions.fillCreateLinkForm(driver, TITLE, URL, CATEGORY_TITLE_1);
        LinkActions.submitCreateLinkForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Hivatkozás elmentve.");
        assertThat(LinkActions.isCreateLinkWindowDisplayed(driver)).isFalse();

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        List<ListItemDetailsItem> detailedListItems = DetailedListActions.getDetailedListItems(driver);
        assertThat(detailedListItems).hasSize(1);
        ListItemDetailsItem textItem = detailedListItems.get(0);
        assertThat(textItem.getTitle()).isEqualTo(TITLE);
        assertThat(textItem.getType()).isEqualTo(ListItemType.LINK);

        textItem.open();
        assertThat(driver.getWindowHandles()).hasSize(2);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(URL));
    }

    @Test
    public void deleteLink() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        LinkActions.createLink(driver, TITLE, URL);

        DetailedListActions.findDetailedItem(driver, TITLE)
            .delete(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }

    @Test
    public void editLink_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        LinkActions.createLink(driver, TITLE, URL);

        DetailedListActions.findDetailedItem(driver, TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, "", NEW_URL, 0);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
    }

    @Test
    public void editLink_emptyUrl() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        LinkActions.createLink(driver, TITLE, URL);

        DetailedListActions.findDetailedItem(driver, TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, NEW_TITLE, "", 0);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifyErrorNotification(driver, "URL nem lehet üres.");
    }

    @Test
    public void editLink() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        LinkActions.createLink(driver, TITLE, URL, CATEGORY_TITLE_1);

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        DetailedListActions.findDetailedItem(driver, TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, NEW_TITLE, NEW_URL, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);

        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();

        DetailedListActions.up(driver);

        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);

        ListItemDetailsItem item = DetailedListActions.findDetailedItem(driver, NEW_TITLE);
        item.open();

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(NEW_URL));
    }
}
