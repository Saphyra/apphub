package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ChecklistTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookPageActions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRow;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistTableCrudTest extends SeleniumTest {
    private static final String TABLE_TITLE = "table-title";
    private static final String COLUMN_NAME_1 = "column-name-1";
    private static final String CATEGORY_TITLE_1 = "category-title";
    private static final String COLUMN_NAME_2 = "column-name-2";
    private static final String COLUMN_1_1 = "column-1-1";
    private static final String COLUMN_1_2 = "column-1-2";
    private static final String COLUMN_2_1 = "column-2-1";
    private static final String COLUMN_2_2 = "column-2-2";
    private static final String NEW_TABLE_TITLE = "new-table-title";
    private static final String COLUMN_NAME_3 = "column-name-3";
    private static final String COLUMN_1_1_NEW = "column-1-1-new";
    private static final String COLUMN_1_2_NEW = "column-1-2-new";
    private static final String COLUMN_2_1_NEW = "column-2-1-new";
    private static final String COLUMN_2_2_NEW = "column-2-2-new";
    private static final String CATEGORY_TITLE_2 = "category-title-2";

    @Test
    public void checklistTableCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        //Create - Blank title
        ChecklistTableActions.openCreateChecklistTableWindow(driver);
        ChecklistTableActions.submitCreateChecklistTableForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(ChecklistTableActions.isCreateChecklistTableWindowDisplayed(driver)).isTrue();

        //Create - Has Blank column name
        ChecklistTableActions.fillNewChecklistTableTitle(driver, TABLE_TITLE);
        ChecklistTableActions.addColumnToNewChecklistTable(driver);
        ChecklistTableActions.fillColumnName(driver, 0, COLUMN_NAME_1);
        ChecklistTableActions.submitCreateChecklistTableForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "Az oszlop neve nem lehet üres.");
        assertThat(ChecklistTableActions.isCreateChecklistTableWindowDisplayed(driver)).isTrue();

        //Create
        ChecklistTableActions.createChecklistTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                new ChecklistTableRow(true, Arrays.asList(COLUMN_1_1, COLUMN_1_2)),
                new ChecklistTableRow(false, Arrays.asList(COLUMN_2_1, COLUMN_2_2))
            ),
            CATEGORY_TITLE_1
        );
        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);
        List<ListItemDetailsItem> detailedListItems = DetailedListActions.getDetailedListItems(driver);
        assertThat(detailedListItems).hasSize(1);
        ListItemDetailsItem tableItem = detailedListItems.get(0);
        assertThat(tableItem.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableItem.getType()).isEqualTo(ListItemType.CHECKLIST_TABLE);

        NotificationUtil.clearNotifications(driver);

        //Edit - Blank title
        ChecklistTableActions.openChecklistTable(driver, TABLE_TITLE);
        ChecklistTableActions.enableEditing(driver);
        ChecklistTableActions.editChecklistTableTitle(driver, "");
        ChecklistTableActions.saveChanges(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(ChecklistTableActions.isEditingEnabled(driver)).isTrue();

        //Edit - Has blank column name
        ChecklistTableActions.editChecklistTableTitle(driver, NEW_TABLE_TITLE);
        ChecklistTableActions.editColumnName(driver, 0, "");
        ChecklistTableActions.saveChanges(driver);
        NotificationUtil.verifyErrorNotification(driver, "Az oszlop neve nem lehet üres.");
        assertThat(ChecklistTableActions.isEditingEnabled(driver)).isTrue();

        //Edit - Discard
        ChecklistTableActions.editChecklistTable(
            driver,
            "asd",
            Arrays.asList("asd", "asd"),
            Arrays.asList(
                new ChecklistTableRow(false, Arrays.asList(COLUMN_1_1, COLUMN_1_2)),
                new ChecklistTableRow(true, Arrays.asList(COLUMN_2_1, COLUMN_2_2))
            )
        );
        ChecklistTableActions.discardChanges(driver);
        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                new ChecklistTableRow(true, Arrays.asList(COLUMN_1_1, COLUMN_1_2)),
                new ChecklistTableRow(false, Arrays.asList(COLUMN_2_1, COLUMN_2_2))
            )
        );

        //Edit
        ChecklistTableActions.enableEditing(driver);
        ChecklistTableActions.editChecklistTableTitle(driver, NEW_TABLE_TITLE);
        ChecklistTableActions.addColumnToEditChecklistTable(driver);
        ChecklistTableActions.editColumnName(driver, 2, COLUMN_NAME_3);
        ChecklistTableActions.addRowToEditChecklistTable(driver);
        ChecklistTableActions.removeColumnFromEditChecklistTable(driver, 1);
        ChecklistTableActions.removeRowFromEditChecklistTable(driver, 1);
        ChecklistTableActions.editColumnValue(driver, 0, 0, COLUMN_1_1_NEW);
        ChecklistTableActions.editColumnValue(driver, 0, 1, COLUMN_1_2_NEW);
        ChecklistTableActions.editColumnValue(driver, 1, 0, COLUMN_2_1_NEW);
        ChecklistTableActions.editColumnValue(driver, 1, 1, COLUMN_2_2_NEW);
        ChecklistTableActions.viewChecklistTableSetRowStatus(driver, 0, false);
        ChecklistTableActions.viewChecklistTableSetRowStatus(driver, 1, true);
        ChecklistTableActions.moveColumnLeft(driver, 1);
        ChecklistTableActions.moveRowUp(driver, 1);
        ChecklistTableActions.saveChanges(driver);
        NotificationUtil.verifySuccessNotification(driver, "Lista-táblázat elmentve.");
        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            NEW_TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_3, COLUMN_NAME_1),
            Arrays.asList(
                new ChecklistTableRow(true, Arrays.asList(COLUMN_2_2_NEW, COLUMN_2_1_NEW)),
                new ChecklistTableRow(false, Arrays.asList(COLUMN_1_2_NEW, COLUMN_1_1_NEW))
            )
        );

        //Check row
        ChecklistTableActions.viewChecklistTableSetRowStatus(driver, 1, true);
        ChecklistTableActions.closeWindow(driver);
        ChecklistTableActions.openChecklistTable(driver, NEW_TABLE_TITLE);
        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            NEW_TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_3, COLUMN_NAME_1),
            Arrays.asList(
                new ChecklistTableRow(true, Arrays.asList(COLUMN_2_2_NEW, COLUMN_2_1_NEW)),
                new ChecklistTableRow(true, Arrays.asList(COLUMN_1_2_NEW, COLUMN_1_1_NEW))
            )
        );

        //Uncheck row
        ChecklistTableActions.viewChecklistTableSetRowStatus(driver, 0, false);
        ChecklistTableActions.closeWindow(driver);
        ChecklistTableActions.openChecklistTable(driver, NEW_TABLE_TITLE);
        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            NEW_TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_3, COLUMN_NAME_1),
            Arrays.asList(
                new ChecklistTableRow(false, Arrays.asList(COLUMN_2_2_NEW, COLUMN_2_1_NEW)),
                new ChecklistTableRow(true, Arrays.asList(COLUMN_1_2_NEW, COLUMN_1_1_NEW))
            )
        );

        //Delete checked
        ChecklistTableActions.deleteCheckedChecklistTableItems(driver);
        NotificationUtil.verifySuccessNotification(driver, "A kijelölt elemek sikeresen törölve.");
        ChecklistTableActions.closeWindow(driver);
        ChecklistTableActions.openChecklistTable(driver, NEW_TABLE_TITLE);
        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            NEW_TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_3, COLUMN_NAME_1),
            Arrays.asList(new ChecklistTableRow(false, Arrays.asList(COLUMN_2_2_NEW, COLUMN_2_1_NEW)))
        );

        NotificationUtil.clearNotifications(driver);
        ChecklistTableActions.closeWindow(driver);

        //Edit as listItem - Blank title
        DetailedListActions.findDetailedItem(driver, NEW_TABLE_TITLE)
            .edit(driver);
        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Edit as listItem
        NotebookPageActions.fillEditListItemDialog(driver, TABLE_TITLE, null, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
        DetailedListActions.up(driver);
        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);
        DetailedListActions.findDetailedItem(driver, TABLE_TITLE);

        //Delete
        DetailedListActions.findDetailedItem(driver, TABLE_TITLE)
            .delete(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }
}
