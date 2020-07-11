package com.github.saphyra.apphub.integration.frontend.service.notebook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class NotebookPage {
    private static final By CREATE_CATEGORY_WINDOW = By.id("create-category");
    private static final By OPEN_CREATE_CATEGORY_WINDOW_BUTTON = By.id("new-category-button");
    private static final By NEW_BUTTON = By.id("new-button");
    private static final By NEW_CATEGORY_TITLE_INPUT = By.id("new-category-title");
    private static final By AVAILABLE_PARENTS = By.cssSelector("#create-category-parent-selection-category-list .category");
    private static final By SAVE_NEW_CATEGORY_BUTTON = By.id("create-category-button");
    private static final By CATEGORY_TREE_ROOT = By.cssSelector("#category-list > .category-wrapper");
    private static final By TITLE_OF_OPENED_CATEGORY = By.id("category-details-title");
    private static final By DETAILED_LIST_ITEMS = By.cssSelector("#category-content-list .list-item-details-item");

    public static WebElement createCategoryWindow(WebDriver driver) {
        return driver.findElement(CREATE_CATEGORY_WINDOW);
    }

    public static WebElement openCreateCategoryWindowButton(WebDriver driver) {
        return driver.findElement(OPEN_CREATE_CATEGORY_WINDOW_BUTTON);
    }

    public static WebElement newButton(WebDriver driver) {
        return driver.findElement(NEW_BUTTON);
    }

    public static WebElement newCategoryTitleInput(WebDriver driver) {
        return driver.findElement(NEW_CATEGORY_TITLE_INPUT);
    }

    public static List<WebElement> getAvailableParents(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS);
    }

    public static WebElement saveNewCategoryButton(WebDriver driver) {
        return driver.findElement(SAVE_NEW_CATEGORY_BUTTON);
    }

    public static WebElement categoryTreeRoot(WebDriver driver) {
        return driver.findElement(CATEGORY_TREE_ROOT);
    }

    public static WebElement titleOfOpenedCategory(WebDriver driver) {
        return driver.findElement(TITLE_OF_OPENED_CATEGORY);
    }

    public static List<WebElement> detailedListItems(WebDriver driver) {
        return driver.findElements(DETAILED_LIST_ITEMS);
    }
}
