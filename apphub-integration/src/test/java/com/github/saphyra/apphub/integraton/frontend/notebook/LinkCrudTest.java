package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookPageActions;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkCrudTest extends SeleniumTest {
    private static final String URL = Endpoints.ACCOUNT_PAGE;
    private static final String TITLE = "title";
    private static final String CATEGORY_TITLE_1 = "category-title-1";
    private static final String CATEGORY_TITLE_2 = "category-title-2";
    private static final String NEW_URL = Endpoints.NOTEBOOK_PAGE;
    private static final String NEW_TITLE = "new-title";

    @Test
    public void linkCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        //Create link
        LinkActions.openCreateLinkWindow(driver);

        //Empty title
        LinkActions.fillCreateLinkForm(driver, "", URL);
        LinkActions.submitCreateLinkForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Empty URL
        LinkActions.fillCreateLinkForm(driver, TITLE, "");
        LinkActions.submitCreateLinkForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "URL nem lehet üres.");

        //Valid
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
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        NotificationUtil.clearNotifications(driver);

        //Edit link
        DetailedListActions.findDetailedItem(driver, TITLE)
            .edit(driver);

        //Empty title
        NotebookPageActions.fillEditListItemDialog(driver, "", NEW_URL, 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Empty URL
        NotebookPageActions.fillEditListItemDialog(driver, NEW_TITLE, "", 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "URL nem lehet üres.");

        //Valid
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
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        //Delete link
        DetailedListActions.findDetailedItem(driver, NEW_TITLE)
            .delete(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }
}
