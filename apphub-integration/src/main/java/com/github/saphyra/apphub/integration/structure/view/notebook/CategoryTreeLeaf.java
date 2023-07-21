package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@RequiredArgsConstructor
public class CategoryTreeLeaf {
    private final WebElement webElement;

    public List<CategoryTreeLeaf> getChildren() {
        if (!hasChildren()) {
            throw new IllegalStateException("Leaf has no children");
        }

        if (!isOpened()) {
            throw new IllegalStateException("Cannot retrieve child of a closed Leaf");
        }

        return webElement.findElements(By.cssSelector(":scope > .notebook-tree-leaf-children > .notebook-tree-leaf-wrapper"))
            .stream()
            .map(CategoryTreeLeaf::new)
            .toList();
    }

    public boolean isArchived() {
        return WebElementUtils.getClasses(webElement.findElement(By.cssSelector(":scope .notebook-tree-leaf")))
            .contains("archived");
    }

    public String getTitle() {
        return webElement.findElement(By.cssSelector(":scope > .notebook-tree-leaf .notebook-tree-leaf-title"))
            .getText();
    }

    public void open() {
        if (isOpened()) {
            throw new IllegalStateException("Category Tree Leaf is already opened.");
        }

        webElement.findElement(By.cssSelector(":scope > .notebook-tree-leaf .notebook-tree-leaf-open-button"))
            .click();
    }

    public boolean hasChildren() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.cssSelector(":scope .notebook-tree-leaf-button")))
            .isPresent();
    }

    public boolean isOpened() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.cssSelector(":scope .notebook-tree-leaf-children")))
            .isPresent();
    }

    public void openCategory(WebDriver driver) {
        String title = getTitle();

        webElement.findElement(By.cssSelector(":scope > .notebook-tree-leaf"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getOpenedCategoryName(driver).equals(title))
            .assertTrue("Category is not opened.");
    }

    public void close() {
        if (!isOpened()) {
            throw new IllegalStateException("Category Tree Leaf is already closed.");
        }

        webElement.findElement(By.cssSelector(":scope > .notebook-tree-leaf .notebook-tree-leaf-close-button"))
            .click();
    }
}
