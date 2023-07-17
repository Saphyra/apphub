package com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static java.util.Objects.isNull;

public class OnlyTitleActions {
    public static void createOnlyTitle(WebDriver driver, String title, String... parentTitles) {
        if (!isCreateOnlyTitleWindowDisplayed(driver)) {
            openCreateOnlyTitleWindow(driver);
        }

        fillNewOnlyTitleTitle(driver, title);

        for (String parentTitle : parentTitles) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.availableParentsForNewOnlyTitle(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }

        submitCreateOnlyTitleForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !NotebookPage.createCategoryWindow(driver).isDisplayed());
    }

    public static void openCreateOnlyTitleWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateOnlyTitleWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPage.createOnlyTitleWindow(driver).isDisplayed());
    }

    public static void submitCreateOnlyTitleForm(WebDriver driver) {
        NotebookPage.saveNewOnlyTitleButton(driver).click();
    }

    public static boolean isCreateOnlyTitleWindowDisplayed(WebDriver driver) {
        return NotebookPage.createOnlyTitleWindow(driver)
            .isDisplayed();
    }

    public static void fillNewOnlyTitleTitle(WebDriver driver, String title) {
        clearAndFill(NotebookPage.newOnlyTitleTitleTitleInput(driver), title);
    }
}
