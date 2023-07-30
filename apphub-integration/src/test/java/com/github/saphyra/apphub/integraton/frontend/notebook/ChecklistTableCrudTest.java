package com.github.saphyra.apphub.integraton.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTableActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ChecklistTableCrudTest extends SeleniumTest {
    private static final String CATEGORY_TITLE = "category";
    private static final String CHECKLIST_TABLE_TITLE = "checklist-table";

    @Test(groups = "notebook")
    public void checklistTableCrud(){
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(driver, CATEGORY_TITLE);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.CHECKLIST_TABLE);

        //Create - Blank title
        NewTableActions.fillTitle(driver, " ");
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Cím nem lehet üres.");

        //Create - Has Blank column name
        NewTableActions.fillTitle(driver, CHECKLIST_TABLE_TITLE);
        NewTableActions.submit(driver);
        ToastMessageUtil.verifyErrorToast(driver, "Az oszlop neve nem lehet üres.");

        //-- Add row


        //-- Remove row
        //-- Add column
        //-- Remove column
        //-- Check row
        //-- Move column
        //-- Move row
        //Create
        //Edit - Blank title
        //Edit - Blank column name
        //Edit - Discard
        //-- Add row
        //-- Remove row
        //-- Add column
        //-- Remove column
        //-- Check row
        //-- Move column
        //-- Move row
        //Check row
        //Uncheck row
        //Delete checked
        //Delete
    }
}
