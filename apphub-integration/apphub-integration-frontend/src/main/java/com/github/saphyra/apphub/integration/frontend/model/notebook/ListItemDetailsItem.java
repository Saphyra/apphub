package com.github.saphyra.apphub.integration.frontend.model.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.service.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.NotebookPageActions;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
public class ListItemDetailsItem {
    private static final By TITLE = By.cssSelector(":scope > span");
    private static final By OPTIONS_BUTTON = By.cssSelector(":scope .list-item-options-button");
    private static final By DELETE_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .delete-button");

    private final WebElement webElement;

    public String getTitle() {
        return getTitleElement().getText();
    }

    private WebElement getTitleElement() {
        return webElement.findElement(TITLE);
    }

    public void delete(WebDriver driver) {
        new Actions(driver)
            .moveToElement(webElement.findElement(OPTIONS_BUTTON))
            .perform();

        WebElement deleteButton = webElement.findElement(DELETE_BUTTON);
        assertThat(deleteButton.isDisplayed()).isTrue();
        deleteButton.click();

        NotebookPageActions.confirmDeletionDialog(driver);
    }

    public void open(WebDriver driver) {
        String title = getTitle();
        getTitleElement().click();
        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getTitleOfOpenedCategory(driver).equals(title))
            .assertTrue();
    }
}
