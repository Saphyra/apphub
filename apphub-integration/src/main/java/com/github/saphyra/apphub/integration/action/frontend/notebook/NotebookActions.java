package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.view.notebook.CategoryTreeLeaf;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Optional;

public class NotebookActions {
    public static void newListItem(int serverPort, WebDriver driver) {
        driver.findElement(By.id("notebook-new-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().startsWith(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_NEW_PAGE, "parent", "")))
            .assertTrue("New ListItem page is not opened");
    }

    public static ListItem findListItemByTitleValidated(WebDriver driver, String title) {
        return AwaitilityWrapper.getOptionalWithWait(() -> findListItemByTitle(driver, title))
            .orElseThrow(() -> new RuntimeException("No listItem found by title '" + title + "'"));
    }

    public static Optional<ListItem> findListItemByTitle(WebDriver driver, String title) {
        return getListItems(driver)
            .stream()
            .filter(listItem -> listItem.getTitle().equals(title))
            .findFirst();
    }

    public static List<ListItem> getListItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#notebook-category-content-list .notebook-content-category-content-list-item"))
            .stream()
            .map(ListItem::new)
            .toList();
    }

    public static CategoryTreeLeaf getCategoryTree(WebDriver driver) {
        return new CategoryTreeLeaf(driver.findElement(By.cssSelector("#notebook-category-tree > .notebook-tree-leaf-wrapper")));
    }

    public static void up(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-up-button"))
            .click();

        SleepUtil.sleep(1000);
    }

    public static void cancelListItemDeletion(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-cancel-deletion-button"))
            .click();
    }

    public static void confirmListItemDeletion(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-delete-list-item-button"))
            .click();
    }

    public static String getOpenedCategoryName(WebDriver driver) {
        return driver.findElement(By.id("notebook-content-category-content-title"))
            .getText();
    }

    public static void search(WebDriver driver, String searchText) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-search")), searchText);

        SleepUtil.sleep(2000);
    }

    public static void selectAll(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-select-all-button"))
            .click();
    }

    public static void unselectAll(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-unselect-all-button"))
            .click();
    }

    public static void archiveSelected(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-archive-selected-button"))
            .click();
    }

    public static void unarchiveSelected(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-unarchive-selected-button"))
            .click();
    }

    public static void pinSelected(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-pin-selected-button"))
            .click();
    }

    public static void unpinSelected(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-unpin-selected-button"))
            .click();
    }

    public static void deleteSelected(WebDriver driver) {
        driver.findElement(By.id("notebook-content-category-content-delete-selected-button"))
            .click();

        driver.findElement(By.id("notebook-delete-selected-items-confirm-button"))
            .click();
    }
}
