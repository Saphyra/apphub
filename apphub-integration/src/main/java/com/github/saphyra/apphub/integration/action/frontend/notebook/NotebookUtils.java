package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewCategoryActions;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import org.openqa.selenium.WebDriver;

import static java.util.Objects.isNull;

public class NotebookUtils {
    public static void newCategory(WebDriver driver, String title, String... parents) {
        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItem(driver, ListItemType.CATEGORY);

        if (isNull(parents)) {
            throw new UnsupportedOperationException("Up is not available yet");
        } else {
            for (String parent : parents) {
                ParentSelectorActions.selectParent(driver, parent);
            }
        }

        NewCategoryActions.create(driver, title);
    }
}
