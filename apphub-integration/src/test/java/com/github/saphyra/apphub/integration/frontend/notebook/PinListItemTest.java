package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class PinListItemTest extends SeleniumTest {
    private static final String CATEGORY_1 = "category-1";
    private static final String CATEGORY_2 = "category-2";

    @Test(groups = {"fe", "notebook"})
    void openParentOfPinnedItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_1);
        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_2, CATEGORY_1);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_1)
            .open(driver);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_2)
            .pin(driver);

        NotebookActions.up(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, CATEGORY_1).isPresent())
            .assertTrue("Not moved up");

        PinActions.getPinnedItems(driver)
            .getFirst()
            .openParent();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getOpenedCategoryName(driver).equals(CATEGORY_1))
            .assertTrue("Parent category was not opened.");
    }
}
