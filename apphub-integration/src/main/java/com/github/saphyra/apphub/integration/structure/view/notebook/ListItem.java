package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class ListItem {
    @Getter
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-title"))
            .getText();
    }

    public void pin(WebDriver driver) {
        if (isPinned()) {
            throw new IllegalStateException("ListItem is already pinned.");
        }

        String title = getTitle();

        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-pin-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitleValidated(driver, title).isPinned())
            .assertTrue("ListItem is not pinned");
    }

    private boolean isPinned() {
        return WebElementUtils.getClasses(webElement)
            .contains("pinned");
    }

    public void archive(WebDriver driver) {
        if (isArchived()) {
            throw new IllegalStateException("ListItem is already archived.");
        }

        String title = getTitle();

        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-archive-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitleValidated(driver, title).isArchived())
            .assertTrue("ListItem is not archived");
    }

    public boolean isArchived() {
        return WebElementUtils.getClasses(webElement)
            .contains("archived");
    }

    public void unarchive(WebDriver driver) {
        if (!isArchived()) {
            throw new IllegalStateException("ListItem is not archived.");
        }

        String title = getTitle();

        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-unarchive-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !NotebookActions.findListItemByTitleValidated(driver, title).isArchived())
            .assertTrue("ListItem is still archived");
    }

    public void edit(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-edit-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().startsWith(UrlFactory.create(Endpoints.NOTEBOOK_EDIT_LIST_ITEM_PAGE)))
            .assertTrue("Edit ListItem page is not opened");
    }

    public void open() {
        open(() -> {
            SleepUtil.sleep(1000);
            return true;
        });
    }

    public void open(Callable<Boolean> verify) {
        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(verify)
            .assertTrue("ListItem is not opened.");
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-delete-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> getDeletionConfirmationDialog(driver)).isPresent())
            .assertTrue("Confirmation dialog is not opened");
    }

    private static WebElement getDeletionConfirmationDialog(WebDriver driver) {
        return driver.findElement(By.id("notebook-content-category-content-delete-list-item-confirmation-dialog"));
    }

    public void deleteWithConfirmation(WebDriver driver) {
        delete(driver);

        driver.findElement(By.id("notebook-content-category-content-delete-list-item-button"))
            .click();
    }

    public void openParent() {
        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-parent-button"))
            .click();

        SleepUtil.sleep(1000);
    }

    public ListItemType getType() {
        List<String> classes = WebElementUtils.getClasses(webElement);

        return Arrays.stream(ListItemType.values())
            .filter(type -> classes.contains(type.getClazz()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Type not recognizable. ClassList: " + classes));

    }

    public void cloneListItem() {
        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-clone-button"))
            .click();

        SleepUtil.sleep(1000);
    }
}
