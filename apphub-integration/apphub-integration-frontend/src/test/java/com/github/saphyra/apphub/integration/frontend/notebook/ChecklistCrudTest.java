package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.frontend.model.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ViewChecklistItem;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.NotebookPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void createChecklist_blankTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        ChecklistActions.openCreateChecklistWindow(driver);
        ChecklistActions.submitCreateChecklistForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(ChecklistActions.isCreateChecklistWindowDisplayed(driver)).isTrue();
    }

    @Test
    public void createChecklist() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);

        ChecklistActions.openCreateChecklistWindow(driver);
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
    }

    @Test
    public void editChecklist_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        ChecklistActions.createChecklist(
            driver,
            CHECKLIST_TITLE,
            Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build())
        );

        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        ChecklistActions.enableEditing(driver);
        ChecklistActions.editChecklistItemTitle(driver, "");
        ChecklistActions.saveChanges(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(ChecklistActions.isEditingEnabled(driver)).isTrue();
    }

    @Test
    public void editChecklist_discard() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        ChecklistActions.createChecklist(
            driver,
            CHECKLIST_TITLE,
            Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build())
        );

        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        ChecklistActions.enableEditing(driver);

        ChecklistActions.editChecklistItemTitle(driver, NEW_CHECKLIST_TITLE);
        ChecklistActions.editChecklistItemContent(driver, CHECKLIST_ITEM_1, NewChecklistItemData.builder().content(CHECKLIST_ITEM_2).checked(false).build());

        ChecklistActions.discardChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ChecklistActions.isEditingEnabled(driver))
            .assertTrue();

        assertThat(ChecklistActions.getTitle(driver)).isEqualTo(CHECKLIST_TITLE);
        List<ViewChecklistItem> newChecklistItems = ChecklistActions.getChecklistItems(driver);
        assertThat(newChecklistItems).hasSize(1);
        assertThat(newChecklistItems.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_1);
        assertThat(newChecklistItems.get(0).isChecked()).isFalse();
    }

    @Test
    public void editChecklist() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        ChecklistActions.createChecklist(
            driver,
            CHECKLIST_TITLE,
            Arrays.asList(
                NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build(),
                NewChecklistItemData.builder().content(CHECKLIST_ITEM_4).checked(false).build()
            )
        );

        ChecklistActions.openChecklist(driver, CHECKLIST_TITLE);
        ChecklistActions.enableEditing(driver);

        ChecklistActions.editChecklistItemTitle(driver, NEW_CHECKLIST_TITLE);
        ChecklistActions.editChecklistItemContent(driver, CHECKLIST_ITEM_1, NewChecklistItemData.builder().content(CHECKLIST_ITEM_2).checked(true).build());
        ChecklistActions.editChecklistRemoveItem(driver, CHECKLIST_ITEM_4);

        ChecklistActions.editChecklistAddItems(driver, Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_3).checked(false).build()));

        ChecklistActions.saveChanges(driver);

        NotificationUtil.verifySuccessNotification(driver, "Lista elmentve.");
        assertThat(ChecklistActions.isEditingEnabled(driver)).isFalse();

        ChecklistActions.closeWindow(driver);
        ChecklistActions.openChecklist(driver, NEW_CHECKLIST_TITLE);

        List<ViewChecklistItem> items = ChecklistActions.getChecklistItems(driver);

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getContent()).isEqualTo(CHECKLIST_ITEM_2);
        assertThat(items.get(0).isChecked()).isTrue();

        assertThat(items.get(1).getContent()).isEqualTo(CHECKLIST_ITEM_3);
        assertThat(items.get(1).isChecked()).isFalse();
    }

    @Test
    public void deleteChecklist() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        ChecklistActions.createChecklist(
            driver,
            CHECKLIST_TITLE,
            Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build())
        );

        DetailedListActions.findDetailedItem(driver, CHECKLIST_TITLE)
            .delete(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }

    @Test
    public void editChecklistItem_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        ChecklistActions.createChecklist(
            driver,
            CHECKLIST_TITLE,
            Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build())
        );

        DetailedListActions.findDetailedItem(driver, CHECKLIST_TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
    }

    @Test
    public void editChecklistItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        ChecklistActions.createChecklist(
            driver,
            CHECKLIST_TITLE,
            Arrays.asList(NewChecklistItemData.builder().content(CHECKLIST_ITEM_1).checked(false).build()),
            CATEGORY_TITLE_1
        );

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        DetailedListActions.findDetailedItem(driver, CHECKLIST_TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, NEW_CHECKLIST_TITLE, null, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);

        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();

        DetailedListActions.up(driver);

        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);

        DetailedListActions.findDetailedItem(driver, NEW_CHECKLIST_TITLE);
    }
}
