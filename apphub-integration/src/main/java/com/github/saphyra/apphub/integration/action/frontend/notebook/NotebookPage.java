package com.github.saphyra.apphub.integration.action.frontend.notebook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class NotebookPage {
    static final String DELETE_CHECKED_ITEMS_CONFIRMATION_DIALOG_ID = "delete-checked-items-confirmation";

    private static final By CREATE_CATEGORY_WINDOW = By.id("create-category");
    private static final By OPEN_CREATE_CATEGORY_WINDOW_BUTTON = By.id("new-category-button");
    private static final By NEW_BUTTON = By.id("new-button");
    private static final By NEW_CATEGORY_TITLE_INPUT = By.id("new-category-title");
    private static final By AVAILABLE_PARENTS_FOR_NEW_CATEGORY = By.cssSelector("#create-category-parent-selection-category-list .category");
    private static final By SAVE_NEW_CATEGORY_BUTTON = By.id("create-category-button");
    private static final By CATEGORY_TREE_ROOT = By.cssSelector("#category-list > .category-wrapper");
    private static final By TITLE_OF_OPENED_CATEGORY = By.id("category-details-title");
    private static final By DETAILED_LIST_ITEMS = By.cssSelector("#category-content-list .list-item-details-item");
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
    private static final By EDIT_LIST_ITEM_DIALOG = By.id("edit-list-item");
    private static final By EDIT_LIST_ITEM_TITLE_INPUT = By.id("edit-list-item-title-input");
    private static final By EDIT_LIST_ITEM_VALUE_INPUT = By.id("edit-list-item-value-input");
    private static final By AVAILABLE_PARENTS_FOR_EDIT_LIST_ITEM = By.cssSelector("#edit-list-item-parent-selection-category-list .category");
    private static final By SAVE_EDITED_LIST_ITEM_BUTTON = By.id("edit-list-item-save-button");
    private static final By DETAILED_LIST_UP_BUTTON = By.id("category-content-parent-selection-parent-button");
    private static final By EDIT_LIST_ITEM_UP_BUTTON = By.id("edit-list-item-parent-selection-parent-button");
    private static final By OPEN_CREATE_CHECKLIST_WINDOW_BUTTON = By.id("new-checklist-button");
    private static final By CREATE_CHECKLIST_WINDOW = By.id("create-checklist");
    private static final By SAVE_NEW_CHECKLIST_BUTTON = By.id("create-checklist-button");
    private static final By NEW_CHECKLIST_TITLE_INPUT = By.id("new-checklist-title");
    private static final By CREATE_CHECKLIST_SELECTED_CATEGORY_WRAPPER = By.id("create-checklist-selected-category-wrapper");
    private static final By AVAILABLE_PARENTS_FOR_NEW_CHECKLIST = By.cssSelector("#create-checklist-parent-selection-category-list .category");
    private static final By CREATE_CHECKLIST_ADD_ITEM_BUTTON = By.id("create-checklist-new-item-button");
    private static final By CREATE_CHECKLIST_LAST_CHECKLIST_ITEM = By.cssSelector("#new-checklist-content-wrapper .checklist-item:last-child");
    private static final By VIEW_CHECKLIST_WINDOW = By.id("view-checklist-content");
    private static final By EDIT_CHECKLIST_BUTTON = By.id("view-checklist-edit-button");
    private static final By VIEW_CHECKLIST_TITLE = By.id("view-checklist-title");
    private static final By SAVE_EDITED_CHECKLIST_BUTTON = By.id("view-checklist-edit-save-button");
    private static final By VIEW_CHECKLIST_ITEMS = By.cssSelector("#view-checklist-content .view-checklist-item");
    private static final By EDIT_CHECKLIST_DISCARD_BUTTON = By.id("view-checklist-edit-cancel-button");
    private static final By EDIT_CHECKLIST_ADD_ITEM_BUTTON = By.id("view-checklist-edit-add-item-button");
    private static final By EDIT_CHECKLIST_LAST_CHECKLIST_ITEM = By.cssSelector("#view-checklist-content .view-checklist-item:last-child");
    private static final By VIEW_CHECKLIST_CLOSE_WINDOW_BUTTON = By.id("view-checklist-close-button");
    private static final By OPEN_CREATE_TABLE_WINDOW_BUTTON = By.id("new-table-button");
    private static final By CREATE_TABLE_WINDOW = By.id("create-table");
    private static final By SAVE_NEW_TABLE_BUTTON = By.id("create-table-button");
    private static final By NEW_TABLE_TITLE_INPUT = By.id("new-table-title");
    private static final By ADD_COLUMN_TO_NEW_TABLE_BUTTON = By.id("create-table-new-column-button");
    private static final By COLUMN_NAMES_FOR_NEW_TABLE = By.cssSelector("#create-table input.column-title");
    private static final By CREATE_TABLE_SELECTED_CATEGORY_WRAPPER = By.id("create-table-selected-category-wrapper");
    private static final By AVAILABLE_PARENTS_FOR_NEW_TABLE = By.cssSelector("#create-table-parent-selection-category-list .create-item-category");
    private static final By ROWS_FOR_NEW_TABLE = By.cssSelector("#new-table-content tr");
    private static final By ADD_ROW_TO_NEW_TABLE_BUTTON = By.id("create-table-new-row-button");
    private static final By VIEW_TABLE_WINDOW = By.id("view-table");
    private static final By EDIT_TABLE_BUTTON = By.id("view-table-edit-button");
    private static final By VIEW_TABLE_TITLE = By.id("view-table-title");
    private static final By SAVE_EDITED_TABLE_BUTTON = By.id("view-table-edit-save-button");
    private static final By COLUMN_NAMES_FOR_VIEW_TABLE = By.cssSelector("#view-table-head-row .column-title");
    private static final By ROWS_FOR_VIEW_TABLE = By.cssSelector("#view-table-content tr");
    private static final By DISCARD_EDIT_TABLE_BUTTON = By.id("view-table-edit-cancel-button");
    private static final By ADD_COLUMN_TO_EDIT_TABLE_BUTTON = By.id("view-table-edit-add-column-button");
    private static final By ADD_ROW_TO_EDIT_TABLE_BUTTON = By.id("view-table-edit-add-row-button");
    private static final By COLUMN_HEADS_FOR_EDIT_TABLE = By.cssSelector("#view-table-head-row .column-head");
    private static final By OPEN_CREATE_CHECKLIST_TABLE_WINDOW_BUTTON = By.id("new-checklist-table-button");
    private static final By CREATE_CHECKLIST_TABLE_WINDOW = By.id("create-checklist-table");
    private static final By SAVE_NEW_CHECKLIST_TABLE_BUTTON = By.id("create-checklist-table-button");
    private static final By NEW_CHECKLIST_TABLE_INPUT = By.id("new-checklist-table-title");
    private static final By ADD_COLUMN_TO_NEW_CHECKLIST_TABLE_BUTTON = By.id("create-checklist-table-new-column-button");
    private static final By COLUMN_NAMES_FOR_NEW_CHECKLIST_TABLE = By.cssSelector("#create-checklist-table input.column-title");
    private static final By CREATE_CHECKLIST_TABLE_SELECTED_CATEGORY_WRAPPER = By.id("create-checklist-table-selected-category-wrapper");
    private static final By AVAILABLE_PARENTS_FOR_NEW_CHECKLIST_TABLE = By.cssSelector("#create-checklist-table-parent-selection-category-list .create-item-category");
    private static final By ADD_ROW_TO_NEW_CHECKLIST_TABLE_BUTTON = By.id("create-checklist-table-new-row-button");
    private static final By VIEW_CHECKLIST_TABLE_WINDOW = By.id("view-checklist-table");
    private static final By EDIT_CHECKLIST_TABLE_BUTTON = By.id("view-checklist-table-edit-button");
    private static final By VIEW_CHECKLIST_TABLE_TITLE = By.id("view-checklist-table-title");
    private static final By SAVE_EDITED_CHECKLIST_TABLE_BUTTON = By.id("view-checklist-table-edit-save-button");
    private static final By ROWS_FOR_NEW_CHECKLIST_TABLE = By.cssSelector("#new-checklist-table-content tr");
    private static final By COLUMN_NAMES_FOR_VIEW_CHECKLIST_TABLE = By.cssSelector("#view-checklist-table input.column-title");
    private static final By DISCARD_EDIT_CHECKLIST_TABLE_BUTTON = By.id("view-checklist-table-edit-cancel-button");
    private static final By ROWS_FOR_VIEW_CHECKLIST_TABLE = By.cssSelector("#view-checklist-table-content tr");
    private static final By ADD_ROW_TO_EDIT_CHECKLIST_TABLE_BUTTON = By.id("view-checklist-table-edit-add-row-button");
    private static final By COLUMN_HEADS_FOR_EDIT_CHECKLIST_TABLE = By.cssSelector("#view-checklist-table-head-row .column-head");
    private static final By ADD_COLUMN_TO_EDIT_CHECKLIST_TABLE_BUTTON = By.id("view-checklist-table-edit-add-column-button");
    private static final By VIEW_CHECKLIST_TABLE_CLOSE_BUTTON = By.id("view-checklist-table-close-button");
    private static final By CLOSE_VIEW_TABLE_WINDOW_BUTTON = By.id("view-table-close-button");
    private static final By CONVERT_TABLE_TO_CHECKLIST_TABLE_BUTTON = By.id("convert-table-to-checklist-table-button");
    private static final By DELETE_CHECKED_CHECKLIST_ITEM_BUTTON = By.id("delete-checked-checklist-items");
    private static final By DELETE_CHECKED_CHECKLIST_TABLE_ITEM_BUTTON = By.id("delete-checked-checklist-table-items");
    private static final By ORDER_CHECKLIST_ITEMS_BUTTON = By.id("order-checklist-items");

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

    public static WebElement editListItemDialog(WebDriver driver) {
        return driver.findElement(EDIT_LIST_ITEM_DIALOG);
    }

    public static WebElement editListItemTitleInput(WebDriver driver) {
        return driver.findElement(EDIT_LIST_ITEM_TITLE_INPUT);
    }

    public static WebElement editListItemValueInput(WebDriver driver) {
        return driver.findElement(EDIT_LIST_ITEM_VALUE_INPUT);
    }

    public static List<WebElement> availableParentsForEditListItem(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_EDIT_LIST_ITEM);
    }

    public static WebElement saveEditedListItemButton(WebDriver driver) {
        return driver.findElement(SAVE_EDITED_LIST_ITEM_BUTTON);
    }

    public static WebElement detailedListUpButton(WebDriver driver) {
        return driver.findElement(DETAILED_LIST_UP_BUTTON);
    }

    public static WebElement editListItemUpButton(WebDriver driver) {
        return driver.findElement(EDIT_LIST_ITEM_UP_BUTTON);
    }

    public static WebElement openCreateChecklistWindowButton(WebDriver driver) {
        return driver.findElement(OPEN_CREATE_CHECKLIST_WINDOW_BUTTON);
    }

    public static WebElement createChecklistWindow(WebDriver driver) {
        return driver.findElement(CREATE_CHECKLIST_WINDOW);
    }

    public static WebElement saveNewChecklistButton(WebDriver driver) {
        return driver.findElement(SAVE_NEW_CHECKLIST_BUTTON);
    }

    public static WebElement newChecklistTitleInput(WebDriver driver) {
        return driver.findElement(NEW_CHECKLIST_TITLE_INPUT);
    }

    public static WebElement createChecklistSelectedCategoryWrapper(WebDriver driver) {
        return driver.findElement(CREATE_CHECKLIST_SELECTED_CATEGORY_WRAPPER);
    }

    public static List<WebElement> getAvailableParentsForNewChecklist(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_NEW_CHECKLIST);
    }

    public static WebElement createChecklistAddItemButton(WebDriver driver) {
        return driver.findElement(CREATE_CHECKLIST_ADD_ITEM_BUTTON);
    }

    public static WebElement createChecklistLastChecklistItem(WebDriver driver) {
        return driver.findElement(CREATE_CHECKLIST_LAST_CHECKLIST_ITEM);
    }

    public static WebElement viewChecklistWindow(WebDriver driver) {
        return driver.findElement(VIEW_CHECKLIST_WINDOW);
    }

    public static WebElement editChecklistButton(WebDriver driver) {
        return driver.findElement(EDIT_CHECKLIST_BUTTON);
    }

    public static WebElement viewChecklistTitle(WebDriver driver) {
        return driver.findElement(VIEW_CHECKLIST_TITLE);
    }

    public static WebElement saveEditedChecklistButton(WebDriver driver) {
        return driver.findElement(SAVE_EDITED_CHECKLIST_BUTTON);
    }

    public static List<WebElement> viewChecklistItems(WebDriver driver) {
        return driver.findElements(VIEW_CHECKLIST_ITEMS);
    }

    public static WebElement editChecklistDiscardButton(WebDriver driver) {
        return driver.findElement(EDIT_CHECKLIST_DISCARD_BUTTON);
    }

    public static WebElement editChecklistAddItemButton(WebDriver driver) {
        return driver.findElement(EDIT_CHECKLIST_ADD_ITEM_BUTTON);
    }

    public static WebElement editChecklistLastChecklistItem(WebDriver driver) {
        return driver.findElement(EDIT_CHECKLIST_LAST_CHECKLIST_ITEM);
    }

    public static WebElement viewChecklistCloseWindowButton(WebDriver driver) {
        return driver.findElement(VIEW_CHECKLIST_CLOSE_WINDOW_BUTTON);
    }

    public static WebElement openCreateTableWindowButton(WebDriver driver) {
        return driver.findElement(OPEN_CREATE_TABLE_WINDOW_BUTTON);
    }

    public static WebElement createTableWindow(WebDriver driver) {
        return driver.findElement(CREATE_TABLE_WINDOW);
    }

    public static WebElement saveNewTableButton(WebDriver driver) {
        return driver.findElement(SAVE_NEW_TABLE_BUTTON);
    }

    public static WebElement newTableTitleInput(WebDriver driver) {
        return driver.findElement(NEW_TABLE_TITLE_INPUT);
    }

    public static WebElement addColumnToNewTableButton(WebDriver driver) {
        return driver.findElement(ADD_COLUMN_TO_NEW_TABLE_BUTTON);
    }

    public static List<WebElement> columnNamesForNewTable(WebDriver driver) {
        return driver.findElements(COLUMN_NAMES_FOR_NEW_TABLE);
    }

    public static WebElement createTableSelectedCategoryWrapper(WebDriver driver) {
        return driver.findElement(CREATE_TABLE_SELECTED_CATEGORY_WRAPPER);
    }

    public static List<WebElement> availableParentsForNewTable(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_NEW_TABLE);
    }

    public static List<WebElement> rowsForNewTable(WebDriver driver) {
        return driver.findElements(ROWS_FOR_NEW_TABLE);
    }

    public static WebElement addRowToNewTableButton(WebDriver driver) {
        return driver.findElement(ADD_ROW_TO_NEW_TABLE_BUTTON);
    }

    public static WebElement viewTableWindow(WebDriver driver) {
        return driver.findElement(VIEW_TABLE_WINDOW);
    }

    public static WebElement editTableButton(WebDriver driver) {
        return driver.findElement(EDIT_TABLE_BUTTON);
    }

    public static WebElement viewTableTitle(WebDriver driver) {
        return driver.findElement(VIEW_TABLE_TITLE);
    }

    public static WebElement saveEditedTableButton(WebDriver driver) {
        return driver.findElement(SAVE_EDITED_TABLE_BUTTON);
    }

    public static List<WebElement> columnNamesForViewTable(WebDriver driver) {
        return driver.findElements(COLUMN_NAMES_FOR_VIEW_TABLE);
    }

    public static List<WebElement> rowsForViewTable(WebDriver driver) {
        return driver.findElements(ROWS_FOR_VIEW_TABLE);
    }

    public static WebElement discardEditTableButton(WebDriver driver) {
        return driver.findElement(DISCARD_EDIT_TABLE_BUTTON);
    }

    public static WebElement addColumnToEditTableButton(WebDriver driver) {
        return driver.findElement(ADD_COLUMN_TO_EDIT_TABLE_BUTTON);
    }

    public static WebElement addRowToEditTableButton(WebDriver driver) {
        return driver.findElement(ADD_ROW_TO_EDIT_TABLE_BUTTON);
    }

    public static List<WebElement> columnHeadsForEditTable(WebDriver driver) {
        return driver.findElements(COLUMN_HEADS_FOR_EDIT_TABLE);
    }

    public static WebElement openCreateChecklistTableWindowButton(WebDriver driver) {
        return driver.findElement(OPEN_CREATE_CHECKLIST_TABLE_WINDOW_BUTTON);
    }

    public static WebElement createChecklistTableWindow(WebDriver driver) {
        return driver.findElement(CREATE_CHECKLIST_TABLE_WINDOW);
    }

    public static WebElement saveNewChecklistTableButton(WebDriver driver) {
        return driver.findElement(SAVE_NEW_CHECKLIST_TABLE_BUTTON);
    }

    public static WebElement newChecklistTableTitleInput(WebDriver driver) {
        return driver.findElement(NEW_CHECKLIST_TABLE_INPUT);
    }

    public static WebElement addColumnToNewChecklistTableButton(WebDriver driver) {
        return driver.findElement(ADD_COLUMN_TO_NEW_CHECKLIST_TABLE_BUTTON);
    }

    public static List<WebElement> columnNamesForNewChecklistTable(WebDriver driver) {
        return driver.findElements(COLUMN_NAMES_FOR_NEW_CHECKLIST_TABLE);
    }

    public static WebElement createChecklistTableSelectedCategoryWrapper(WebDriver driver) {
        return driver.findElement(CREATE_CHECKLIST_TABLE_SELECTED_CATEGORY_WRAPPER);
    }

    public static List<WebElement> availableParentsForNewChecklistTable(WebDriver driver) {
        return driver.findElements(AVAILABLE_PARENTS_FOR_NEW_CHECKLIST_TABLE);
    }

    public static WebElement addRowToNewChecklistTableButton(WebDriver driver) {
        return driver.findElement(ADD_ROW_TO_NEW_CHECKLIST_TABLE_BUTTON);
    }

    public static WebElement viewChecklistTableWindow(WebDriver driver) {
        return driver.findElement(VIEW_CHECKLIST_TABLE_WINDOW);
    }

    public static WebElement editChecklistTableButton(WebDriver driver) {
        return driver.findElement(EDIT_CHECKLIST_TABLE_BUTTON);
    }

    public static WebElement viewChecklistTableTitle(WebDriver driver) {
        return driver.findElement(VIEW_CHECKLIST_TABLE_TITLE);
    }

    public static WebElement saveEditedChecklistTableButton(WebDriver driver) {
        return driver.findElement(SAVE_EDITED_CHECKLIST_TABLE_BUTTON);
    }

    public static List<WebElement> rowsForNewChecklistTable(WebDriver driver) {
        return driver.findElements(ROWS_FOR_NEW_CHECKLIST_TABLE);
    }

    public static List<WebElement> columnNamesForViewChecklistTable(WebDriver driver) {
        return driver.findElements(COLUMN_NAMES_FOR_VIEW_CHECKLIST_TABLE);
    }

    public static WebElement discardEditChecklistTableButton(WebDriver driver) {
        return driver.findElement(DISCARD_EDIT_CHECKLIST_TABLE_BUTTON);
    }

    public static List<WebElement> rowsForViewChecklistTable(WebDriver driver) {
        return driver.findElements(ROWS_FOR_VIEW_CHECKLIST_TABLE);
    }

    public static WebElement addRowToEditChecklistTableButton(WebDriver driver) {
        return driver.findElement(ADD_ROW_TO_EDIT_CHECKLIST_TABLE_BUTTON);
    }

    public static List<WebElement> columnHeadsForEditChecklistTable(WebDriver driver) {
        return driver.findElements(COLUMN_HEADS_FOR_EDIT_CHECKLIST_TABLE);
    }

    public static WebElement addColumnToEditChecklistTableButton(WebDriver driver) {
        return driver.findElement(ADD_COLUMN_TO_EDIT_CHECKLIST_TABLE_BUTTON);
    }

    public static WebElement viewChecklistTableCloseButton(WebDriver driver) {
        return driver.findElement(VIEW_CHECKLIST_TABLE_CLOSE_BUTTON);
    }

    public static WebElement closeViewTableWindowButton(WebDriver driver) {
        return driver.findElement(CLOSE_VIEW_TABLE_WINDOW_BUTTON);
    }

    public static WebElement convertTableToChecklistTableButton(WebDriver driver) {
        return driver.findElement(CONVERT_TABLE_TO_CHECKLIST_TABLE_BUTTON);
    }

    public static WebElement deleteCheckedChecklistItemsButton(WebDriver driver) {
        return driver.findElement(DELETE_CHECKED_CHECKLIST_ITEM_BUTTON);
    }

    public static WebElement deleteCheckedChecklistTableItemsButton(WebDriver driver) {
        return driver.findElement(DELETE_CHECKED_CHECKLIST_TABLE_ITEM_BUTTON);
    }

    public static WebElement orderChecklistItemsButton(WebDriver driver) {
        return driver.findElement(ORDER_CHECKLIST_ITEMS_BUTTON);
    }

    public static WebElement closeEditListItemWindowButton(WebDriver driver) {
        return driver.findElement(By.id("edit-list-item-close-button"));
    }

    public static WebElement closeCreateTableButton(WebDriver driver) {
        return driver.findElement(By.id("create-table-close-button"));
    }

    public static List<WebElement> pinnedItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#pinned-items .pinned-item"));
    }

    public static WebElement categoryContent(WebDriver driver) {
        return driver.findElement(By.id("category-content"));
    }

    public static WebElement searchInput(WebDriver driver) {
        return driver.findElement(By.id("search-container-title"));
    }

    public static WebElement openCreateOnlyTitleWindowButton(WebDriver driver) {
        return driver.findElement(By.id("new-only-title-button"));
    }

    public static WebElement createOnlyTitleWindow(WebDriver driver) {
        return driver.findElement(By.id("create-only-title"));
    }

    public static WebElement saveNewOnlyTitleButton(WebDriver driver) {
        return driver.findElement(By.id("create-only-title-button"));
    }

    public static WebElement newOnlyTitleTitleTitleInput(WebDriver driver) {
        return driver.findElement(By.id("new-only-title-title"));
    }

    public static List<WebElement> availableParentsForNewOnlyTitle(WebDriver driver) {
        return driver.findElements(By.cssSelector("#create-only-title-parent-selection-category-list .category"));
    }

    public static WebElement settingMenuToggleButton(WebDriver driver) {
        return driver.findElement(By.id("settings-container-toggle-button"));
    }

    public static WebElement settingMenu(WebDriver driver) {
        return driver.findElement(By.id("settings-container"));
    }

    public static WebElement showArchivedCheckbox(WebDriver driver) {
        return driver.findElement(By.id("settings-show-archived-input"));
    }
}
