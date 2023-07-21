package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewCategoryActions;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import org.openqa.selenium.WebDriver;

public class NotebookUtils {
    public static void newCategory(WebDriver driver, String title) {
        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.CATEGORY);
        NewCategoryActions.create(driver, title);
    }
}
