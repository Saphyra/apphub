package com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static java.util.Objects.isNull;

public class LinkActions {
    public static void createLink(WebDriver driver, String title, String url, String... parents) {
        openCreateLinkWindow(driver);
        fillCreateLinkForm(driver, title, url, parents);
        submitCreateLinkForm(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> !isCreateLinkWindowDisplayed(driver))
            .assertTrue("Failed creating link");
    }

    public static void openCreateLinkWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateLinkWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPage.createLinkWindow(driver).isDisplayed());
    }

    public static void fillCreateLinkForm(WebDriver driver, String title, String url, String... categories) {
        clearAndFill(NotebookPage.createLinkTitleInput(driver), title);
        clearAndFill(NotebookPage.createLinkUrlInput(driver), url);
        selectCategoryForNewLink(driver, categories);
    }

    public static void selectCategoryForNewLink(WebDriver driver, String... categories) {
        for (String parentTitle : categories) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.availableParentsForNewLink(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }
    }

    public static void submitCreateLinkForm(WebDriver driver) {
        NotebookPage.saveNewLinkButton(driver).click();
    }

    public static boolean isCreateLinkWindowDisplayed(WebDriver driver) {
        return NotebookPage.createLinkWindow(driver).isDisplayed();
    }
}