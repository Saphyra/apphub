package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import static java.util.Objects.isNull;

public class LinkActions {
    public static void createLink(WebDriver driver, String title, String url) {
        openCreateLinkWindow(driver);
        fillCreateLinkForm(driver, title, url);
        submitCreateLinkForm(driver);
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
        NotebookPage.createLinkTitleInput(driver).sendKeys(title);
        NotebookPage.createLinkUrlInput(driver).sendKeys(url);
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
