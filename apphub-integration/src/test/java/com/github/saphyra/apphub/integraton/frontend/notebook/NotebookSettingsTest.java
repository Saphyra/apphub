package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookSettingActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.CategoryTreeLeaf;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotebookSettingsTest extends SeleniumTest {
    private static final String CATEGORY_1 = "category-1";
    private static final String CATEGORY_2 = "category-2";

    @Test(groups = {"fe", "notebook"})
    void showAndHideArchived() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_1);
        NotebookUtils.newCategory(driver, CATEGORY_2);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_2)
            .archive(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_1)
            .pin(driver);
        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_2)
            .pin(driver);

        //Hide archived items
        NotebookSettingActions.openSettings(driver);

        NotebookSettingActions.hideArchived(driver);

        assertThat(NotebookActions.getListItems(driver)).extracting(ListItem::getTitle).containsExactly(CATEGORY_1);
        assertThat(NotebookActions.getPinnedItems(driver)).extracting(ListItem::getTitle).containsExactly(CATEGORY_1);
        assertThat(NotebookActions.getCategoryTree(driver).getChildren()).extracting(CategoryTreeLeaf::getTitle).containsExactly(CATEGORY_1);

        //Show archived items
        NotebookSettingActions.showArchived(driver);

        assertThat(NotebookActions.getListItems(driver)).extracting(ListItem::getTitle).containsExactly(CATEGORY_1, CATEGORY_2);
        assertThat(NotebookActions.getPinnedItems(driver)).extracting(ListItem::getTitle).containsExactly(CATEGORY_1, CATEGORY_2);
        assertThat(NotebookActions.getCategoryTree(driver).getChildren()).extracting(CategoryTreeLeaf::getTitle).containsExactly(CATEGORY_1, CATEGORY_2);
    }
}
