package com.github.saphyra.apphub.integration.frontend.model.notebook;

import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.frontend.service.notebook.NotebookPageActions;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
public class ListItemDetailsItem {
    private static final By TITLE = By.cssSelector(":scope > span");
    private static final By OPTIONS_BUTTON = By.cssSelector(":scope .list-item-options-button");
    private static final By DELETE_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .delete-button");
    private static final By EDIT_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .edit-button");

    private final WebElement webElement;

    public String getTitle() {
        return getTitleElement().getText();
    }

    private WebElement getTitleElement() {
        return webElement.findElement(TITLE);
    }

    public void edit(WebDriver driver) {
        openOptionsMenu(driver);

        WebElement editButton = webElement.findElement(EDIT_BUTTON);
        assertThat(editButton.isDisplayed()).isTrue();
        editButton.click();

        NotebookPageActions.verifyEditListItemDialogOpened(driver);
    }

    public void delete(WebDriver driver) {
        openOptionsMenu(driver);

        WebElement deleteButton = webElement.findElement(DELETE_BUTTON);
        assertThat(deleteButton.isDisplayed()).isTrue();
        deleteButton.click();

        NotebookPageActions.confirmDeletionDialog(driver);
    }

    private void openOptionsMenu(WebDriver driver) {
        new Actions(driver)
            .moveToElement(webElement.findElement(OPTIONS_BUTTON))
            .perform();
    }

    public void open() {
        getTitleElement().click();
    }

    public ListItemType getType() {
        List<String> classList = Arrays.stream(webElement.getAttribute("class")
            .split(" "))
            .collect(Collectors.toList());
        return Arrays.stream(ListItemType.values())
            .filter(type -> classList.contains(type.getCssClass()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Type not recognizable. ClassList: " + classList));
    }
}
