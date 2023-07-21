package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.view.notebook.CategoryTreeLeaf;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NotebookActions {
    public static void newListItem(WebDriver driver) {
        driver.findElement(By.id("notebook-new-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().startsWith(UrlFactory.create(Endpoints.NOTEBOOK_NEW_PAGE, "parent", "")))
            .assertTrue("New ListItem page is not opened");
    }

    public static ListItem findListItemByTitle(WebDriver driver, String title) {
        return driver.findElements(By.cssSelector("#notebook-category-content-list .notebook-content-category-content-list-item"))
            .stream()
            .map(ListItem::new)
            .filter(listItem -> listItem.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No listItem found by title '" + title + "'"));
    }

    public static ListItem findPinnedItemByTitle(WebDriver driver, String title) {
        return driver.findElements(By.cssSelector("#notebook-pinned-items .notebook-content-category-content-list-item"))
            .stream()
            .map(ListItem::new)
            .filter(listItem -> listItem.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No pinned listItem found by title '" + title + "'"));
    }

    public static CategoryTreeLeaf getCategoryTree(WebDriver driver) {
        return new CategoryTreeLeaf(driver.findElement(By.id("notebook-category-tree")));
    }
}
