package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderListItemTest extends SeleniumTest {
    private static final String CATEGORY_NAME_1 = "category-name-1";
    private static final String CATEGORY_NAME_2 = "category-name-2";
    private static final String LIST_ITEM_TITLE_1 = "list-item-title-1";
    private static final String LIST_ITEM_TITLE_2 = "list-item-title-2";

    @Test(groups = {"fe", "notebook"})
    public void orderListItems() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_NAME_1);
        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_TITLE_1);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_NAME_1)
            .archive(driver);
        NotebookActions.findListItemByTitleValidated(driver, LIST_ITEM_TITLE_1)
            .archive(driver);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_NAME_1);
        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_NAME_2);
        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_TITLE_1);
        NotebookUtils.newOnlyTitle(getServerPort(), driver, LIST_ITEM_TITLE_2);

        CustomAssertions.assertList(
            NotebookActions.getListItems(driver),
            listItem -> assertThat(listItem)
                .returns(CATEGORY_NAME_1, ListItem::getTitle)
                .returns(false, ListItem::isArchived),
            listItem -> assertThat(listItem)
                .returns(CATEGORY_NAME_2, ListItem::getTitle)
                .returns(false, ListItem::isArchived),
            listItem -> assertThat(listItem)
                .returns(LIST_ITEM_TITLE_1, ListItem::getTitle)
                .returns(false, ListItem::isArchived),
            listItem -> assertThat(listItem)
                .returns(LIST_ITEM_TITLE_2, ListItem::getTitle)
                .returns(false, ListItem::isArchived),
            listItem -> assertThat(listItem)
                .returns(CATEGORY_NAME_1, ListItem::getTitle)
                .returns(true, ListItem::isArchived),
            listItem -> assertThat(listItem)
                .returns(LIST_ITEM_TITLE_1, ListItem::getTitle)
                .returns(true, ListItem::isArchived)
        );
    }
}
