package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.notebook.NewChecklistItem;
import com.github.saphyra.apphub.integration.frontend.model.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ViewChecklistItem;
import com.github.saphyra.apphub.integration.frontend.service.common.CommonPageActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistActions {
    public static void createChecklist(WebDriver driver, String checklistTitle, List<NewChecklistItemData> checklistItemDataList, String... categories) {
        openCreateChecklistWindow(driver);
        fillNewChecklistTitle(driver, checklistTitle);
        newChecklistAddItems(driver, checklistItemDataList);
        selectCategoryForNewChecklist(driver, categories);
        submitCreateChecklistForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !isCreateChecklistWindowDisplayed(driver));
    }

    public static void openCreateChecklistWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateChecklistWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPage.createChecklistWindow(driver).isDisplayed());
    }

    public static void submitCreateChecklistForm(WebDriver driver) {
        NotebookPage.saveNewChecklistButton(driver).click();
    }

    public static boolean isCreateChecklistWindowDisplayed(WebDriver driver) {
        return NotebookPage.createChecklistWindow(driver).isDisplayed();
    }

    public static void fillNewChecklistTitle(WebDriver driver, String checklistTitle) {
        clearAndFill(NotebookPage.newChecklistTitleInput(driver), checklistTitle);
    }

    public static void selectCategoryForNewChecklist(WebDriver driver, String... categories) {
        new Actions(driver)
            .moveToElement(NotebookPage.createChecklistSelectedCategoryWrapper(driver))
            .perform();
        for (String parentTitle : categories) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.getAvailableParentsForNewChecklist(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }
    }

    public static void newChecklistAddItems(WebDriver driver, List<NewChecklistItemData> items) {
        items.forEach(newChecklistItemData -> {
            NotebookPage.createChecklistAddItemButton(driver).click();
            NewChecklistItem lastItem = new NewChecklistItem(NotebookPage.createChecklistLastChecklistItem(driver));
            lastItem.setContent(newChecklistItemData.getContent());
            lastItem.setStatus(newChecklistItemData.isChecked());
        });
    }

    public static void openChecklist(WebDriver driver, String checklistTitle) {
        DetailedListActions.findDetailedItem(driver, checklistTitle).open();

        AwaitilityWrapper.createDefault()
            .until(() -> isViewChecklistWindowOpened(driver))
            .assertTrue();
    }

    private static Boolean isViewChecklistWindowOpened(WebDriver driver) {
        return NotebookPage.viewChecklistWindow(driver).isDisplayed();
    }

    public static void enableEditing(WebDriver driver) {
        assertThat(isViewChecklistWindowOpened(driver)).isTrue();
        if (!isEditingEnabled(driver)) {
            NotebookPage.editChecklistButton(driver).click();
        }
        assertThat(isEditingEnabled(driver)).isTrue();
    }

    public static boolean isEditingEnabled(WebDriver driver) {
        return !NotebookPage.editChecklistButton(driver)
            .isDisplayed();
    }

    public static void editChecklistItemTitle(WebDriver driver, String newTitle) {
        assertThat(isEditingEnabled(driver)).isTrue();
        WebElement title = NotebookPage.viewChecklistTitle(driver);
        assertThat(title.getAttribute("contenteditable")).isEqualTo("true");
        title.clear();
        title.sendKeys(newTitle);
    }

    public static void saveChanges(WebDriver driver) {
        WebElement saveButton = NotebookPage.saveEditedChecklistButton(driver);
        assertThat(saveButton.isDisplayed()).isTrue();
        saveButton.click();
    }

    public static void editChecklistItemContent(WebDriver driver, String originalContent, NewChecklistItemData newChecklistItemData) {
        ViewChecklistItem item = getChecklistItem(driver, originalContent);

        item.setContent(newChecklistItemData.getContent());
        item.setStatus(newChecklistItemData.isChecked());
    }

    public static ViewChecklistItem getChecklistItem(WebDriver driver, String originalContent) {
        return getChecklistItems(driver)
            .stream()
            .filter(newChecklistItem -> newChecklistItem.getContent().equals(originalContent))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ViewChecklistItem not found with content" + originalContent));
    }

    public static void discardChanges(WebDriver driver) {
        WebElement discardButton = NotebookPage.editChecklistDiscardButton(driver);
        assertThat(discardButton.isDisplayed()).isTrue();
        discardButton.click();
    }

    public static String getTitle(WebDriver driver) {
        return NotebookPage.viewChecklistTitle(driver).getText();
    }

    public static List<ViewChecklistItem> getChecklistItems(WebDriver driver) {
        return NotebookPage.viewChecklistItems(driver)
            .stream()
            .map(ViewChecklistItem::new)
            .collect(Collectors.toList());

    }

    public static void editChecklistAddItems(WebDriver driver, List<NewChecklistItemData> items) {
        items.forEach(newChecklistItemData -> {
            NotebookPage.editChecklistAddItemButton(driver).click();
            ViewChecklistItem lastItem = new ViewChecklistItem(NotebookPage.editChecklistLastChecklistItem(driver));
            lastItem.setContent(newChecklistItemData.getContent());
            lastItem.setStatus(newChecklistItemData.isChecked());
        });
    }

    public static void closeWindow(WebDriver driver) {
        NotebookPage.viewChecklistCloseWindowButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> !isViewChecklistWindowOpened(driver))
            .assertTrue("Could not close ViewChecklistWindow.");
    }

    public static void editChecklistRemoveItem(WebDriver driver, String checklistItemContent) {
        getChecklistItems(driver)
            .stream()
            .filter(viewChecklistItem -> viewChecklistItem.getContent().equals(checklistItemContent))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ViewChecklistItem not found with content " + checklistItemContent))
            .remove();
    }

    public static void deleteCheckedChecklistItems(WebDriver driver) {
        NotebookPage.deleteCheckedChecklistItemsButton(driver)
            .click();

        CommonPageActions.confirmDeletionDialog(driver, NotebookPage.DELETE_CHECKED_ITEMS_CONFIRMATION_DIALOG_ID);
    }
}
