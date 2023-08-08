package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NotebookSettingActions {
    public static void openSettings(WebDriver driver) {
        WebElement button = getToggleSettingsButton(driver);

        if (WebElementUtils.getClasses(button).contains("active")) {
            throw new IllegalStateException("Settings panel is already opened");
        }

        button.click();
    }

    private static WebElement getToggleSettingsButton(WebDriver driver) {
        return driver.findElement(By.id("notebook-toggle-display-settings-button"));
    }

    public static void hideArchived(WebDriver driver) {
        WebElement checkbox = getShowArchivedCheckbox(driver);

        if (!checkbox.isSelected()) {
            throw new IllegalStateException("Archived items are already hidden");
        }

        checkbox.click();

        SleepUtil.sleep(1000);
    }

    private static WebElement getShowArchivedCheckbox(WebDriver driver) {
        return driver.findElement(By.id("notebook-settings-user-show-archived"));
    }

    public static void showArchived(WebDriver driver) {
        WebElement checkbox = getShowArchivedCheckbox(driver);

        if (checkbox.isSelected()) {
            throw new IllegalStateException("Archived items are already displayed");
        }

        checkbox.click();
        SleepUtil.sleep(1000);
    }
}
