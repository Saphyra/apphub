package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;

public class SettingActions {
    public static void openSettingMenu(WebDriver driver) {
        NotebookPage.settingMenuToggleButton(driver)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPage.settingMenu(driver).isDisplayed())
            .assertTrue("Settings menu is not opened.");
    }

    public static void toggleShowArchived(WebDriver driver) {
        NotebookPage.showArchivedCheckbox(driver)
            .click();
    }
}
