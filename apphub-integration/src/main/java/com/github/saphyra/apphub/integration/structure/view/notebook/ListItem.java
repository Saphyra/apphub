package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class ListItem {
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
            .until(() -> NotebookActions.findListItemByTitle(driver, title).isPinned())
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
            .until(() -> NotebookActions.findListItemByTitle(driver, title).isArchived())
            .assertTrue("ListItem is not archived");
    }

    public boolean isArchived() {
        return WebElementUtils.getClasses(webElement)
            .contains("archived");
    }

    public void unarchive(WebDriver driver) {
        if(!isArchived()){
            throw new IllegalStateException("ListItem is not archived.");
        }

        String title = getTitle();

        webElement.findElement(By.cssSelector(":scope .notebook-content-category-content-list-item-unarchive-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !NotebookActions.findListItemByTitle(driver, title).isArchived())
            .assertTrue("ListItem is still archived");
    }
}
