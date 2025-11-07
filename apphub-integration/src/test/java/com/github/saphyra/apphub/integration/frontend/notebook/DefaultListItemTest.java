package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookSettingActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTextActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultListItemTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category";
    private static final String ONLY_TITLE_TITLE = "only-title";
    private static final String LINK_TITLE = "link";
    private static final String LINK_URL = "url";
    private static final String TEXT_TITLE = "text";
    private static final String TEXT_CONTENT = "content";

    @Test(groups = {"notebook", "fe"})
    public void defaultListItem() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver mainDriver = drivers.get(0);
        WebDriver controlDriver = drivers.get(1);

        Navigation.toIndexPage(getServerPort(), mainDriver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(mainDriver, userData);

        ModulesPageActions.openModule(getServerPort(), mainDriver, ModuleLocation.NOTEBOOK);

        //Create list items
        NotebookUtils.newCategory(getServerPort(), mainDriver, CATEGORY_TITLE);
        NotebookUtils.newOnlyTitle(getServerPort(), mainDriver, ONLY_TITLE_TITLE, CATEGORY_TITLE);
        NotebookUtils.newLink(getServerPort(), mainDriver, LINK_TITLE, LINK_URL, CATEGORY_TITLE);
        NotebookUtils.newText(getServerPort(), mainDriver, TEXT_TITLE, TEXT_CONTENT, CATEGORY_TITLE);

        //Set default list item
        NotebookSettingActions.openSettings(mainDriver);
        NotebookSettingActions.editDefaultListItem(mainDriver);
        ParentSelectorActions.selectParent(mainDriver, CATEGORY_TITLE);
        AwaitilityWrapper.awaitAssert(() -> assertThat(ParentSelectorActions.getAvailableParents(mainDriver)).extracting(WebElement::getText).containsExactly(TEXT_TITLE));
        ParentSelectorActions.selectParent(mainDriver, TEXT_TITLE);
        NotebookSettingActions.saveDefaultListItem(mainDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(NotebookSettingActions.getDefaultListItemTitle(mainDriver)).isEqualTo(TEXT_TITLE));

        //Check if default list item is loaded
        Navigation.toIndexPage(getServerPort(), controlDriver);
        IndexPageActions.login(getServerPort(), controlDriver, LoginParameters.fromRegistrationParameters(userData));
        ModulesPageActions.openModule(getServerPort(), controlDriver, ModuleLocation.NOTEBOOK);

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(ViewTextActions.isTextItemOpened(controlDriver)).isTrue();
            assertThat(ViewTextActions.getTitle(controlDriver)).isEqualTo(TEXT_TITLE);
            assertThat(ViewTextActions.getContent(controlDriver)).isEqualTo(TEXT_CONTENT);
        });
    }
}
