package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TextActions {
    public static void createText(WebDriver driver, String textTitle, String textContent, String... categories) {
        if (!isCreateTextWindowDisplayed(driver)) {
            openCreateTextWindow(driver);
        }

        fillNewTextTitle(driver, textTitle);
        fillNewTextContent(driver, textContent);
        selectCategoryForNewText(driver, categories);
        submitCreateTextForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !NotebookPage.createTextWindow(driver).isDisplayed());
    }

    public static void openCreateTextWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateTextWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPage.createTextWindow(driver).isDisplayed());
    }

    public static void submitCreateTextForm(WebDriver driver) {
        NotebookPage.saveNewTextButton(driver).click();
    }

    public static boolean isCreateTextWindowDisplayed(WebDriver driver) {
        return NotebookPage.createTextWindow(driver).isDisplayed();
    }

    public static void fillNewTextTitle(WebDriver driver, String textTitle) {
        NotebookPage.newTextTitleInput(driver).sendKeys(textTitle);
    }

    public static void fillNewTextContent(WebDriver driver, String textContent) {
        NotebookPage.newTextContentInput(driver).sendKeys(textContent);
    }

    public static void selectCategoryForNewText(WebDriver driver, String... categories) {
        new Actions(driver)
            .moveToElement(NotebookPage.createTextSelectedCategoryWrapper(driver))
            .perform();
        for (String parentTitle : categories) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.getAvailableParentsForNewText(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }
    }

    public static boolean isViewTextWindowOpened(WebDriver driver) {
        return NotebookPage.viewTextWindow(driver).isDisplayed();
    }

    public static void openText(WebDriver driver, String textTitle) {
        DetailedListActions.findDetailedItem(driver, textTitle).open();

        AwaitilityWrapper.createDefault()
            .until(() -> isViewTextWindowOpened(driver))
            .assertTrue();
    }

    public static void enableEditing(WebDriver driver) {
        assertThat(isViewTextWindowOpened(driver)).isTrue();
        if (!isEditingEnabled(driver)) {
            NotebookPage.editTextButton(driver).click();
        }
        assertThat(isEditingEnabled(driver)).isTrue();
    }

    public static void editTitle(WebDriver driver, String newTitle) {
        assertThat(isEditingEnabled(driver)).isTrue();
        WebElement title = NotebookPage.viewTextTitle(driver);
        assertThat(title.getAttribute("contenteditable")).isEqualTo("true");
        title.clear();
        title.sendKeys(newTitle);
    }

    public static void editContent(WebDriver driver, String newContent) {
        assertThat(isEditingEnabled(driver)).isTrue();
        WebElement content = NotebookPage.viewTextContent(driver);
        assertThat(content.getAttribute("readonly")).isNotEqualTo("true");
        content.clear();
        content.sendKeys(newContent);
    }

    public static void saveChanges(WebDriver driver) {
        WebElement saveButton = NotebookPage.saveEditedTextButton(driver);
        assertThat(saveButton.isDisplayed()).isTrue();
        saveButton.click();
    }

    public static boolean isEditingEnabled(WebDriver driver) {
        return !NotebookPage.editTextButton(driver)
            .isDisplayed();
    }

    public static void discardChanges(WebDriver driver) {
        WebElement discardButton = NotebookPage.discardEditTextButton(driver);
        assertThat(discardButton.isDisplayed()).isTrue();
        discardButton.click();
        CommonPageActions.confirmConfirmationDialog(driver, "discard-confirmation-dialog");
    }

    public static String getTitle(WebDriver driver) {
        return NotebookPage.viewTextTitle(driver)
            .getText();
    }

    public static String getContent(WebDriver driver) {
        return NotebookPage.viewTextContent(driver)
            .getAttribute("value");
    }

    public static void closeView(WebDriver driver) {
        NotebookPage.viewTextCloseButton(driver).click();
    }
}
