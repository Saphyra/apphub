package com.github.saphyra.apphub.integration.structure.notebook;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.NotebookPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.PinnedItemActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
@Slf4j
public class ListItemDetailsItem {
    private static final String CONFIRMATION_DIALOG_ID = "deletion-confirmation-dialog";

    private static final By TITLE = By.cssSelector(":scope > span");
    private static final By OPTIONS_BUTTON = By.cssSelector(":scope .list-item-options-button");
    private static final By DELETE_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .delete-button");
    private static final By EDIT_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .edit-button");
    private static final By CLONE_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .clone-button");
    private static final By PIN_BUTTON = By.cssSelector(":scope .list-item-options-button-list-wrapper .pin-button");

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

        CommonPageActions.confirmConfirmationDialog(driver, CONFIRMATION_DIALOG_ID);
    }

    public void cloneItem(WebDriver driver) {
        openOptionsMenu(driver);

        WebElement cloneButton = webElement.findElement(CLONE_BUTTON);
        assertThat(cloneButton.isDisplayed()).isTrue();
        cloneButton.click();
    }

    private void openOptionsMenu(WebDriver driver) {
        if (webElement.findElement(By.cssSelector(":scope .list-item-options-button-list-wrapper")).isDisplayed()) {
            return;
        }

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

    public String getId() {
        return webElement.getAttribute("id");
    }

    public void pin(WebDriver driver) {
        String title = getTitle();

        openOptionsMenu(driver);

        WebElement cloneButton = webElement.findElement(PIN_BUTTON);
        assertThat(cloneButton.isDisplayed()).isTrue();
        cloneButton.click();

        AwaitilityWrapper.createDefault()
            .until(() -> PinnedItemActions.getPinnedItems(driver).stream().anyMatch(pinnedItem -> pinnedItem.getTitle().equals(title)))
            .assertTrue(title + " was not pinned.");
    }

    public boolean isPinned() {
        return WebElementUtils.getClasses(webElement.findElement(PIN_BUTTON)).contains("pinned");
    }

    public void openParentCategory() {
        webElement.findElement(By.cssSelector(":scope .list-item-open-parent-button"))
            .click();
    }

    public void archive(WebDriver driver) {
        openOptionsMenu(driver);

        WebElement cloneButton = webElement.findElement(By.cssSelector(":scope .list-item-options-button-list-wrapper .archive-button"));
        assertThat(cloneButton.isDisplayed()).isTrue();
        cloneButton.click();
    }

    public boolean isArchived() {
        return WebElementUtils.getClasses(webElement).contains("archived");
    }
}
