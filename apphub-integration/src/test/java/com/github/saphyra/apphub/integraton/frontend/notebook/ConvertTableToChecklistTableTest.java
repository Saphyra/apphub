package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.ChecklistTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.TableActions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRow;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertTableToChecklistTableTest extends SeleniumTest {
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

    @Test
    public void convertTableToChecklistTable() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TableActions.createTable(
            driver,
            TITLE,
            Arrays.asList(COLUMN_NAME),
            Arrays.asList(
                Arrays.asList(COLUMN_VALUE)
            )
        );

        TableActions.openTable(driver, TITLE);
        TableActions.convertToChecklistTable(driver);

        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            TITLE,
            Arrays.asList(COLUMN_NAME),
            Arrays.asList(new ChecklistTableRow(false, Arrays.asList(COLUMN_VALUE)))
        );

        ChecklistTableActions.viewChecklistTableSetRowStatus(driver, 0, true);
        ChecklistTableActions.closeWindow(driver);

        ListItemDetailsItem listItem = DetailedListActions.findDetailedItem(driver, TITLE);
        assertThat(listItem.getType()).isEqualTo(ListItemType.CHECKLIST_TABLE);
        ChecklistTableActions.openChecklistTable(driver, TITLE);

        ChecklistTableActions.verifyViewChecklistTable(
            driver,
            TITLE,
            Arrays.asList(COLUMN_NAME),
            Arrays.asList(new ChecklistTableRow(true, Arrays.asList(COLUMN_VALUE)))
        );
    }
}
