package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ParentSelectorActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewChecklistActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistCrudTest extends SeleniumTest {
    private static final String CHECKLIST_TITLE = "checklist";
    private static final String CATEGORY_TITLE = "category";
    private static final String CHECKLIST_VALUE_1 = "value-1";
    private static final String CHECKLIST_VALUE_2 = "value-2";
    private static final String NEW_CHECKLIST_TITLE = "new-checklist";
    private static final String NEW_VALUE = "new-value";

    @Test(groups = {"fe", "notebook"})
    public void checklistCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.CHECKLIST);

        //Create - Empty title
        NewChecklistActions.fillTitle(driver, " ");
        NewChecklistActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);

        //Create
        NewChecklistActions.fillTitle(driver, CHECKLIST_TITLE);
        ParentSelectorActions.selectParent(driver, CATEGORY_TITLE);

        //-- Remove item
        NewChecklistActions.getItems(driver)
            .get(0)
            .remove();

        assertThat(NewChecklistActions.getItems(driver)).isEmpty();

        //-- Remove item
        NewChecklistActions.addItem(driver);
        NewChecklistActions.addItem(driver);
        assertThat(NewChecklistActions.getItems(driver)).hasSize(2);

        //-- Fill item
        NewChecklistActions.fillItem(driver, 0, CHECKLIST_VALUE_1);
        NewChecklistActions.fillItem(driver, 1, CHECKLIST_VALUE_2);

        assertThat(NewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(CHECKLIST_VALUE_1, CHECKLIST_VALUE_2);

        //-- Move item down
        NewChecklistActions.getItems(driver)
            .get(0)
            .moveDown();

        assertThat(NewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(CHECKLIST_VALUE_2, CHECKLIST_VALUE_1);

        //-- Move item up
        NewChecklistActions.getItems(driver)
            .get(1)
            .moveUp();

        assertThat(NewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(CHECKLIST_VALUE_1, CHECKLIST_VALUE_2);

        //-- Check item
        NewChecklistActions.getItems(driver)
            .get(0)
            .check();

        assertThat(NewChecklistActions.getItems(driver)).extracting(ChecklistItem::isChecked).containsExactly(true, false);

        //-- Uncheck item
        NewChecklistActions.getItems(driver)
            .get(0)
            .uncheck();

        assertThat(NewChecklistActions.getItems(driver)).extracting(ChecklistItem::isChecked).containsExactly(false, false);

        NewChecklistActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE))
            .assertTrue("Checklist was not created.");

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .open();

        //Edit - Empty title
        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TITLE)
            .open(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-checklist"))).isPresent());

        ViewChecklistActions.enableEditing(driver);
        ViewChecklistActions.fillTitle(driver, " ");
        ViewChecklistActions.saveChanges(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);

        //Edit - Discard changes
        ViewChecklistActions.fillTitle(driver, NEW_CHECKLIST_TITLE);
        ChecklistItem checklistItem = ViewChecklistActions.getItems(driver)
            .get(0);
        checklistItem.check();
        checklistItem.setValue(NEW_VALUE);
        NewChecklistActions.getItems(driver)
            .get(1)
            .remove();

        ViewChecklistActions.discardChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> ViewChecklistActions.getItems(driver).size() == 2)
            .assertTrue("Changes were not discarded");

        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(CHECKLIST_VALUE_1, CHECKLIST_VALUE_2);
        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::isChecked).containsExactly(false, false);

        //Edit
        ViewChecklistActions.enableEditing(driver);

        ViewChecklistActions.fillTitle(driver, NEW_CHECKLIST_TITLE);
        checklistItem = ViewChecklistActions.getItems(driver)
            .get(0);
        checklistItem.check();
        checklistItem.setValue(NEW_VALUE);
        ViewChecklistActions.getItems(driver)
            .get(1)
            .remove();

        ViewChecklistActions.saveChanges(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> !ViewChecklistActions.isEditingEnabled(driver))
            .assertTrue("Editing is still enabled.");

        ViewChecklistActions.close(driver);

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, NEW_CHECKLIST_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("ListItem not found with title " + NEW_CHECKLIST_TITLE))
            .open(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-checklist"))).isPresent());

        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(NEW_VALUE);
        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::isChecked).containsExactly(true);

        //Order items
        ViewChecklistActions.enableEditing(driver);
        ViewChecklistActions.addItem(driver);
        ViewChecklistActions.addItem(driver);
        ViewChecklistActions.fillItem(driver, 1, CHECKLIST_VALUE_2);
        ViewChecklistActions.fillItem(driver, 2, CHECKLIST_VALUE_1);
        ViewChecklistActions.saveChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> ViewChecklistActions.getItems(driver).size() == 3)
            .assertTrue("Checklist modifications are not saved.");

        ViewChecklistActions.orderItems(driver);

        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(NEW_VALUE, CHECKLIST_VALUE_1, CHECKLIST_VALUE_2);

        //Delete checked
        ViewChecklistActions.deleteChecked(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> ViewChecklistActions.getItems(driver).size() == 2)
            .assertTrue("Item was not deleted");

        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::getValue).containsExactly(CHECKLIST_VALUE_1, CHECKLIST_VALUE_2);

        //Check item
        ViewChecklistActions.getItems(driver)
            .get(0)
            .check();

        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::isChecked).containsExactly(true, false);

        //Uncheck item
        ViewChecklistActions.getItems(driver)
            .get(0)
            .uncheck();

        assertThat(ViewChecklistActions.getItems(driver)).extracting(ChecklistItem::isChecked).containsExactly(false, false);

        //Delete
        ViewChecklistActions.close(driver);

        NotebookActions.findListItemByTitleValidated(driver, NEW_CHECKLIST_TITLE)
            .deleteWithConfirmation(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Checklist is not deleted.");
    }
}
