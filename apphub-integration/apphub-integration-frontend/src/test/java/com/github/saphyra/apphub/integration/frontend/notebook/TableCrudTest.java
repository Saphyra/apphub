package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.NotebookPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.TableActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TableCrudTest extends SeleniumTest {
    private static final String TABLE_TITLE = "table-title";
    private static final String COLUMN_NAME_1 = "column-name-1";
    private static final String COLUMN_NAME_2 = "column-name-2";
    private static final String COLUMN_NAME_3 = "column-name-3";
    private static final String CATEGORY_TITLE_1 = "category-1";
    private static final String COLUMN_1_1 = "column-1-1";
    private static final String COLUMN_1_2 = "column-1-2";
    private static final String COLUMN_2_1 = "column-2-1";
    private static final String COLUMN_2_2 = "column-2-2";
    private static final String NEW_TABLE_TITLE = "new-table-title";
    private static final String COLUMN_1_1_NEW = "column-1-1-new";
    private static final String COLUMN_1_2_NEW = "column-1-2-new";
    private static final String COLUMN_2_1_NEW = "column-2-1-new";
    private static final String COLUMN_2_2_NEW = "column-2-2-new";
    private static final String CATEGORY_TITLE_2 = "category-title-2";

    @Test
    public void createTable_blankTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.openCreateTableWindow(driver);
        TableActions.submitCreateTableForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(TableActions.isCreateTableWindowDisplayed(driver)).isTrue();
    }

    @Test
    public void createTable_hasBlankColumnName() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.openCreateTableWindow(driver);
        TableActions.fillNewTableTitle(driver, TABLE_TITLE);
        TableActions.addColumnToNewTable(driver);
        TableActions.fillColumnName(driver, 0, COLUMN_NAME_1);
        TableActions.submitCreateTableForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "Az oszlop neve nem lehet üres.");
        assertThat(TableActions.isCreateTableWindowDisplayed(driver)).isTrue();
    }

    @Test
    public void createTable() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);

        TableActions.openCreateTableWindow(driver);
        TableActions.fillNewTableTitle(driver, TABLE_TITLE);
        TableActions.selectCategoryForNewTable(driver, CATEGORY_TITLE_1);
        TableActions.addColumnToNewTable(driver);
        TableActions.fillColumnName(driver, 0, COLUMN_NAME_1);
        TableActions.fillColumnName(driver, 1, COLUMN_NAME_2);
        TableActions.submitCreateTableForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Táblázat elmentve.");

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        List<ListItemDetailsItem> detailedListItems = DetailedListActions.getDetailedListItems(driver);
        assertThat(detailedListItems).hasSize(1);
        ListItemDetailsItem textItem = detailedListItems.get(0);
        assertThat(textItem.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(textItem.getType()).isEqualTo(ListItemType.TABLE);
    }

    @Test
    public void editTable_blankTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );

        TableActions.openTable(driver, TABLE_TITLE);

        TableActions.enableEditing(driver);
        TableActions.editTableTitle(driver, "");
        TableActions.saveChanges(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(TableActions.isEditingEnabled(driver)).isTrue();
    }

    @Test
    public void editTable_hasBlankColumnName() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );

        TableActions.openTable(driver, TABLE_TITLE);

        TableActions.enableEditing(driver);

        TableActions.editColumnName(driver, 0, "");

        TableActions.saveChanges(driver);

        NotificationUtil.verifyErrorNotification(driver, "Az oszlop neve nem lehet üres.");
        assertThat(TableActions.isEditingEnabled(driver)).isTrue();
    }

    @Test
    public void editTable_discard() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );

        TableActions.openTable(driver, TABLE_TITLE);

        TableActions.editTable(
            driver,
            "asd",
            Arrays.asList("asd", "asd"),
            Arrays.asList(
                Arrays.asList("asd", "asd"),
                Arrays.asList("asd", "asd")
            )
        );

        TableActions.discardChanges(driver);
        TableActions.verifyViewTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );
    }

    @Test
    public void editTable() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );

        TableActions.openTable(driver, TABLE_TITLE);
        TableActions.enableEditing(driver);

        TableActions.editTableTitle(driver, NEW_TABLE_TITLE);

        TableActions.addColumnToEditTable(driver);
        TableActions.editColumnName(driver, 2, COLUMN_NAME_3);

        TableActions.addRowToEditTable(driver);

        TableActions.removeColumnFromEditTable(driver, 1);
        TableActions.removeRowFromEditTable(driver, 1);

        TableActions.editColumnValue(driver, 0, 0, COLUMN_1_1_NEW);
        TableActions.editColumnValue(driver, 0, 1, COLUMN_1_2_NEW);
        TableActions.editColumnValue(driver, 1, 0, COLUMN_2_1_NEW);
        TableActions.editColumnValue(driver, 1, 1, COLUMN_2_2_NEW);

        TableActions.moveColumnLeft(driver, 1);
        TableActions.moveRowUp(driver, 1);

        TableActions.saveChanges(driver);

        NotificationUtil.verifySuccessNotification(driver, "Táblázat elmentve.");

        TableActions.verifyViewTable(
            driver,
            NEW_TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_3, COLUMN_NAME_1),
            Arrays.asList(
                Arrays.asList(COLUMN_2_2_NEW, COLUMN_2_1_NEW),
                Arrays.asList(COLUMN_1_2_NEW, COLUMN_1_1_NEW)
            )
        );
    }

    @Test
    public void deleteTable() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );

        DetailedListActions.findDetailedItem(driver, TABLE_TITLE)
            .delete(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }

    @Test
    public void editTableAsListItem_blankTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            )
        );

        DetailedListActions.findDetailedItem(driver, TABLE_TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
    }

    @Test
    public void editTableAsListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        TableActions.createTable(
            driver,
            TABLE_TITLE,
            Arrays.asList(COLUMN_NAME_1, COLUMN_NAME_2),
            Arrays.asList(
                Arrays.asList(COLUMN_1_1, COLUMN_1_2),
                Arrays.asList(COLUMN_2_1, COLUMN_2_2)
            ),
            CATEGORY_TITLE_1
        );

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        DetailedListActions.findDetailedItem(driver, TABLE_TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, NEW_TABLE_TITLE, null, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);

        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();

        DetailedListActions.up(driver);

        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);

        DetailedListActions.findDetailedItem(driver, NEW_TABLE_TITLE);
    }
}
