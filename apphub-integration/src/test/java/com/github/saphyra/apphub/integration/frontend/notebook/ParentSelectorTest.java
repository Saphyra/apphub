package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParentSelectorTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String PARENT_TITLE = "parent-title";

    @Test(groups = {"notebook", "fe"})
    public void selectParentAsDefaultIfListItemNotACategory() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, PARENT_TITLE);
        NotebookUtils.newText(driver, TITLE, CONTENT, PARENT_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, PARENT_TITLE)
            .open();
        NotebookActions.findListItemByTitleValidated(driver, TITLE)
            .open();

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItemType(driver, ListItemType.TEXT);

        assertThat(ParentSelectorActions.getParent(driver)).isEqualTo(PARENT_TITLE);
    }
}
