package com.github.saphyra.apphub.integraton.frontend.notebook_deprecated;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.ChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.NotebookPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.structure.api.notebook.ViewChecklistItem;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class ChecklistCrudTest extends SeleniumTest {
    private static final String CATEGORY_TITLE_1 = "category-title-1";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String CHECKLIST_ITEM_1 = "checklist-item-1";
    private static final String CHECKLIST_ITEM_2 = "checklist-item-2";
    private static final String NEW_CHECKLIST_TITLE = "new-checklist-title";
    private static final String CHECKLIST_ITEM_3 = "checklist-item-3";
    private static final String CATEGORY_TITLE_2 = "category-title-2";
    private static final String CHECKLIST_ITEM_4 = "checklist-item-4";

    @Test
    public void checklistCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        //Create - Empty title
        ChecklistActions.openCreateChecklistWindow(driver);
        ChecklistActions.submitCreateChecklistForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(ChecklistActions.isCreateChecklistWindowDisplayed(driver)).isTrue();

        //Create
        ChecklistActions.fillNewChecklistTitle(driver, CHECKLIST_TITLE);
        ChecklistActions.selectCategoryForNewChecklist(driver, CATEGORY_TITLE_1);
        ChecklistActions.newChecklistAddItems(
            driver,
            Arrays.asList(
                NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build(),
                NewChecklistItemData.builder().content(CHECKLIST_ITEM_2).checked(true).build()
            )
        );
        ChecklistActions.submitCreateChecklistForm(driver);
        NotificationUtil.verifySuccessNotification(driver, "Lista elmentve.");
        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);
        List<ListItemDetailsItem> detailedListItems = DetailedListActions.getDetailedListItems(driver);
        assertThat(detailedListItems).hasSize(1);
        ListItemDetailsItem textItem = detailedListItems.get(0);
        assertThat(textItem.getTitle()).isEqualTo(CHECKLIST_TITLE);
        assertThat(textItem.getType()).isEqualTo(ListItemType.CHECKLIST);

        NotificationUtil.clearNotifications(driver);
        //Edit - Empty title
        detailedListItems.get(0).open();
        ChecklistActions.enableEditing(driver);
        ChecklistActions.editChecklistItemTitle(driver, "");
        ChecklistActions.saveChanges(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(ChecklistActions.isEditingEnabled(driver)).isTrue();

        //Edit - Discard
        ChecklistActions.editChecklistItemTitle(driver, NEW_CHECKLIST_TITLE);
        ChecklistActions.editChecklistItemContent(driver, CHECKLIST_ITEM_1, NewChecklistItemData.builder().content(CHECKLIST_ITEM_2).checked(false).build());
        ChecklistActions.discardChanges(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> !ChecklistActions.isEditingEnabled(driver))
            .assertTrue("Checklist remained editable.");
        assertThat(ChecklistActions.getTitle(driver)).isEqualTo(CHECKLIST_TITLE);
        List<ViewChecklistItem> newChecklistItems = ChecklistActions.getChecklistItems(driver);
        assertThat(newChecklistItems).hasSize(2);
        assertThat(newChecklistItems.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_1);
        assertThat(newChecklistItems.get(0).isChecked()).isFalse();
        assertThat(newChecklistItems.get(1).getContent()).isEqualTo(CHECKLIST_ITEM_2);
        assertThat(newChecklistItems.get(1).isChecked()).isTrue();

        //Edit
        ChecklistActions.enableEditing(driver);
        ChecklistActions.editChecklistItemTitle(driver, NEW_CHECKLIST_TITLE);
        ChecklistActions.editChecklistItemContent(driver, CHECKLIST_ITEM_1, NewChecklistItemData.builder().content(CHECKLIST_ITEM_4).checked(true).build());
        ChecklistActions.editChecklistRemoveItem(driver, CHECKLIST_ITEM_2);
        ChecklistActions.editChecklistAddItems(driver, Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_3).checked(false).build()));
        ChecklistActions.saveChanges(driver);
        NotificationUtil.verifySuccessNotification(driver, "Lista elmentve.");
        assertThat(ChecklistActions.isEditingEnabled(driver)).isFalse();
        ChecklistActions.closeWindow(driver);
        ChecklistActions.openChecklist(driver, NEW_CHECKLIST_TITLE);
        List<ViewChecklistItem> items = ChecklistActions.getChecklistItems(driver);
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_4);
        assertThat(items.get(0).isChecked()).isTrue();
        assertThat(items.get(1).getContent()).isEqualTo(CHECKLIST_ITEM_3);
        assertThat(items.get(1).isChecked()).isFalse();

        NotificationUtil.clearNotifications(driver);

        //Edit as listItem - Empty title
        ChecklistActions.closeWindow(driver);
        DetailedListActions.findDetailedItem(driver, NEW_CHECKLIST_TITLE)
            .edit(driver);
        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Edit as listItem
        NotebookPageActions.fillEditListItemDialog(driver, CHECKLIST_TITLE, null, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
        DetailedListActions.up(driver);
        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);
        DetailedListActions.findDetailedItem(driver, CHECKLIST_TITLE);

        //Check item
        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        ChecklistActions.getChecklistItem(driver, CHECKLIST_ITEM_3).setStatus(true);
        ChecklistActions.closeWindow(driver);
        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        assertThat(ChecklistActions.getChecklistItem(driver, CHECKLIST_ITEM_3).isChecked()).isTrue();

        //Uncheck item
        ChecklistActions.getChecklistItem(driver, CHECKLIST_ITEM_4).setStatus(false);
        ChecklistActions.closeWindow(driver);
        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        assertThat(ChecklistActions.getChecklistItem(driver, CHECKLIST_ITEM_4).isChecked()).isFalse();

        //Order items
        ChecklistActions.orderItems(driver);
        NotificationUtil.verifySuccessNotification(driver, "Sorba rendezés sikeres.");
        ChecklistActions.closeWindow(driver);
        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        List<ViewChecklistItem> checklistItems = ChecklistActions.getChecklistItems(driver);
        assertThat(checklistItems).hasSize(2);
        assertThat(checklistItems.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_3);
        assertThat(checklistItems.get(1).getContent()).isEqualTo(CHECKLIST_ITEM_4);

        //Delete checked
        ChecklistActions.deleteCheckedChecklistItems(driver);
        NotificationUtil.verifySuccessNotification(driver, "A kijelölt elemek sikeresen törölve.");
        ChecklistActions.closeWindow(driver);
        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        checklistItems = ChecklistActions.getChecklistItems(driver);
        assertThat(checklistItems).hasSize(1);
        assertThat(checklistItems.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_4);

        //Delete checklist
        ChecklistActions.closeWindow(driver);
        DetailedListActions.findDetailedItem(driver, CHECKLIST_TITLE)
            .delete(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }
}