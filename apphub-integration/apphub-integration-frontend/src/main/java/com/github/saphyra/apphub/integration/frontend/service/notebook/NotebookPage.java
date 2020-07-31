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
    private static final By AVAILABLE_PARENTS_FOR_NEW_CATEGORY = By.cssSelector("#create-category-parent-selection-category-list .category");
    private static final By SAVE_NEW_CATEGORY_BUTTON = By.id("create-category-button");
    private static final By CATEGORY_TREE_ROOT = By.cssSelector("#category-list > .category-wrapper");
    private static final By TITLE_OF_OPENED_CATEGORY = By.id("category-details-title");
    private static final By DETAILED_LIST_ITEMS = By.cssSelector("#category-content-list .list-item-details-item");
    private static final By DELETION_CONFIRMATION_DIALOG = By.id("deletion-confirmation-dialog");
    private static final By OPEN_CREATE_TEXT_WINDOW_BUTTON = By.id("new-text-button");
    private static final By CREATE_TEXT_WINDOW = By.id("create-text");
    private static final By SAVE_NEW_TEXT_BUTTON = By.id("create-text-button");
    private static final By NEW_TEXT_TITLE_INPUT = By.id("new-text-title");
    private static final By NEW_TEXT_CONTENT_INPUT = By.id("new-text-content");
    private static final By AVAILABLE_PARENTS_FOR_NEW_TEXT = By.cssSelector("#create-text-parent-selection-category-list .category");
    private static final By CREATE_TEXT_SELECTED_CATEGORY_WRAPPER = By.id("create-text-selected-category-wrapper");
    private static final By VIEW_TEXT_WINDOW = By.id("view-text");
    private static final By EDIT_TEXT_BUTTON = By.id("view-text-edit-button");
    private static final By VIEW_TEXT_TITLE = By.id("view-text-title");
    private static final By SAVE_EDITED_TEXT_BUTTON = By.id("view-text-edit-save-button");
    private static final By VIEW_TEXT_CONTENT = By.id("view-text-content");
    private static final By DISCARD_EDIT_TEXT_BUTTON = By.id("view-text-edit-cancel-button");
    private static final By VIEW_TEXT_CLOSE_BUTTON = By.id("view-text-close-button");
    private static final By OPEN_CREATE_LINK_WINDOW_BUTTON = By.id("new-link-button");
    private static final By CREATE_LINK_WINDOW = By.id("create-link");
    private static final By CREATE_LINK_TITLE_INPUT = By.id("new-link-title");
    private static final By CREATE_LINK_URL_INPUT = By.id("new-link-url");
    private static final By AVAILABLE_PARENTS_FOR_NEW_LINK = By.cssSelector("#create-link-parent-selection-category-list .category");
    private static final By SAVE_NEW_LINK_BUTTON = By.id("create-link-button");

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

    public static List<WebElement> availableParentsForNewCategory(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_NEW_CATEGORY);
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

    public static WebElement deletionConfirmationDialog(WebDriver driver) {
        return driver.findElement(DELETION_CONFIRMATION_DIALOG);
    }

    public static WebElement openCreateTextWindowButton(WebDriver driver) {
        return driver.findElement(OPEN_CREATE_TEXT_WINDOW_BUTTON);
    }

    public static WebElement createTextWindow(WebDriver driver) {
        return driver.findElement(CREATE_TEXT_WINDOW);
    }

    public static WebElement saveNewTextButton(WebDriver driver) {
        return driver.findElement(SAVE_NEW_TEXT_BUTTON);
    }

    public static WebElement newTextTitleInput(WebDriver driver) {
        return driver.findElement(NEW_TEXT_TITLE_INPUT);
    }

    public static WebElement newTextContentInput(WebDriver driver) {
        return driver.findElement(NEW_TEXT_CONTENT_INPUT);
    }

    public static List<WebElement> getAvailableParentsForNewText(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_NEW_TEXT);
    }

    public static WebElement createTextSelectedCategoryWrapper(WebDriver driver) {
        return driver.findElement(CREATE_TEXT_SELECTED_CATEGORY_WRAPPER);
    }

    public static WebElement viewTextWindow(WebDriver driver) {
        return driver.findElement(VIEW_TEXT_WINDOW);
    }

    public static WebElement editTextButton(WebDriver driver) {
        return driver.findElement(EDIT_TEXT_BUTTON);
    }

    public static WebElement viewTextTitle(WebDriver driver) {
        return driver.findElement(VIEW_TEXT_TITLE);
    }

    public static WebElement saveEditedTextButton(WebDriver driver) {
        return driver.findElement(SAVE_EDITED_TEXT_BUTTON);
    }

    public static WebElement viewTextContent(WebDriver driver) {
        return driver.findElement(VIEW_TEXT_CONTENT);
    }

    public static WebElement discardEditTextButton(WebDriver driver) {
        return driver.findElement(DISCARD_EDIT_TEXT_BUTTON);
    }

    public static WebElement viewTextCloseButton(WebDriver driver) {
        return driver.findElement(VIEW_TEXT_CLOSE_BUTTON);
    }

    public static WebElement openCreateLinkWindowButton(WebDriver driver) {
        return driver.findElement(OPEN_CREATE_LINK_WINDOW_BUTTON);
    }

    public static WebElement createLinkWindow(WebDriver driver) {
        return driver.findElement(CREATE_LINK_WINDOW);
    }

    public static WebElement createLinkTitleInput(WebDriver driver) {
        return driver.findElement(CREATE_LINK_TITLE_INPUT);
    }

    public static WebElement createLinkUrlInput(WebDriver driver) {
        return driver.findElement(CREATE_LINK_URL_INPUT);
    }

    public static List<WebElement> availableParentsForNewLink(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_NEW_LINK);
    }

    public static WebElement saveNewLinkButton(WebDriver driver) {
        return driver.findElement(SAVE_NEW_LINK_BUTTON);
    }
}