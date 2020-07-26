package com.github.saphyra.apphub.integration.frontend.notebook;

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
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkCrudTest extends SeleniumTest {
    private static final String URL = Endpoints.ACCOUNT_PAGE;
    private static final String TITLE = "title";
    private static final String CATEGORY_TITLE = "category-title";

    @Test
    public void createLink_emptyTitle(){
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
    public void createLink_emptyUrl(){
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
    public void createText(){
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE);

        LinkActions.openCreateLinkWindow(driver);
        LinkActions.fillCreateLinkForm(driver, TITLE, URL, CATEGORY_TITLE);
        LinkActions.submitCreateLinkForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Hivatkozás elmentve.");
        assertThat(LinkActions.isCreateLinkWindowDisplayed(driver)).isFalse();

        CategoryActions.openCategory(driver, CATEGORY_TITLE);

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
    public void deleteLink(){
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
}
