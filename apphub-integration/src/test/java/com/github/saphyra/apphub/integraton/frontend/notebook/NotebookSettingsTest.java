package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.OnlyTitleActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinnedItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.SettingActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class NotebookSettingsTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category-title";
    private static final String ONLY_TITLE_TITLE = "only-title-title";

    @Test
    public void notebookSettings() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE);
        OnlyTitleActions.createOnlyTitle(driver, ONLY_TITLE_TITLE);

        DetailedListActions.findDetailedItem(driver, CATEGORY_TITLE)
            .archive(driver);

        DetailedListActions.findDetailedItem(driver, CATEGORY_TITLE)
            .pin(driver);

        //Hide archived items
        SettingActions.openSettingMenu(driver);
        SettingActions.toggleShowArchived(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).size() == 1)
            .assertTrue("Archived list item is not hidden.");

        AwaitilityWrapper.createDefault()
            .until(() -> PinnedItemActions.getPinnedItems(driver).isEmpty())
            .assertTrue("Archived pinned item is not hidden.");

        AwaitilityWrapper.createDefault()
            .until(() -> CategoryActions.getCategoryTreeRoot(driver).getChildren().isEmpty())
            .assertTrue("Archived category tree item is not hidden.");

        driver.navigate()
            .refresh();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).size() == 1)
            .assertTrue("Archived list item is not hidden.");

        AwaitilityWrapper.createDefault()
            .until(() -> PinnedItemActions.getPinnedItems(driver).isEmpty())
            .assertTrue("Archived pinned item is not hidden.");

        AwaitilityWrapper.createDefault()
            .until(() -> CategoryActions.getCategoryTreeRoot(driver).getChildren().isEmpty())
            .assertTrue("Archived category tree item is not hidden.");

        //Show archived items
        SettingActions.openSettingMenu(driver);
        SettingActions.toggleShowArchived(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getDetailedListItems(driver).size() == 2)
            .assertTrue("Archived list item is not displayed.");

        AwaitilityWrapper.createDefault()
            .until(() -> PinnedItemActions.getPinnedItems(driver).size() == 1)
            .assertTrue("Archived pinned item is not displayed.");

        AwaitilityWrapper.createDefault()
            .until(() -> CategoryActions.getCategoryTreeRoot(driver).getChildren().size() == 1)
            .assertTrue("Archived category tree item is not displayed.");
    }
}
