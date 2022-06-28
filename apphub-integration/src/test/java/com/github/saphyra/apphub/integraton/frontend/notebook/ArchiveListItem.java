package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ChecklistTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.TextActions;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRow;
import com.github.saphyra.apphub.integration.structure.notebook.NewChecklistItemData;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveListItem extends SeleniumTest {
    private static final String ROOT_TITLE = "root-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = Endpoints.ACCOUNT_PAGE;
    private static final String TEXT_CONTENT = "text-content";
    private static final String CHECKLIST_ITEM_CONTENT = "checklist-item-content";
    private static final String TABLE_COLUMN_NAME = "table-column-name";
    private static final String TABLE_COLUMN_VALUE = "table-column-value";
    private static final String TEXT_TITLE = "text-title";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String TABLE_TITLE = "table-title";
    private static final String CHECKLIST_TABLE_TITLE = "checklist-table-title";
    private static final String CHECKLIST_TABLE_COLUMN_NAME = "checklist-table-column-name";
    private static final String CHECKLIST_TABLE_COLUMN_VALUE = "checklist-table-column-value";

    @Test
    public void archiveListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, ROOT_TITLE);
        LinkActions.createLink(driver, LINK_TITLE, LINK_URL);
        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);
        ChecklistActions.createChecklist(driver, CHECKLIST_TITLE, Arrays.asList(new NewChecklistItemData(CHECKLIST_ITEM_CONTENT, true)));
        TableActions.createTable(driver, TABLE_TITLE, Arrays.asList(TABLE_COLUMN_NAME), Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)));
        ChecklistTableActions.createChecklistTable(driver, CHECKLIST_TABLE_TITLE, Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME), Arrays.asList(new ChecklistTableRow(true, Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))));

        //Archive items
        Stream.of(ROOT_TITLE, LINK_TITLE, TEXT_TITLE, CHECKLIST_TITLE, TABLE_TITLE, CHECKLIST_TABLE_TITLE)
            .forEach(title -> DetailedListActions.findDetailedItem(driver, title).archive(driver));

        Stream.of(ROOT_TITLE, LINK_TITLE, TEXT_TITLE, CHECKLIST_TITLE, TABLE_TITLE, CHECKLIST_TABLE_TITLE)
            .forEach(title -> assertThat(DetailedListActions.findDetailedItem(driver, title).isArchived()).isTrue());

        //Unarchive items
        Stream.of(ROOT_TITLE, LINK_TITLE, TEXT_TITLE, CHECKLIST_TITLE, TABLE_TITLE, CHECKLIST_TABLE_TITLE)
            .forEach(title -> DetailedListActions.findDetailedItem(driver, title).archive(driver));

        Stream.of(ROOT_TITLE, LINK_TITLE, TEXT_TITLE, CHECKLIST_TITLE, TABLE_TITLE, CHECKLIST_TABLE_TITLE)
            .forEach(title -> assertThat(DetailedListActions.findDetailedItem(driver, title).isArchived()).isFalse());
    }
}
