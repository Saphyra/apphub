package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class NotebookNewListItemActions {
    public static void selectListItemType(WebDriver driver, ListItemType type) {
        driver.findElement(By.id(String.format("notebook-new-%s", type.getSelector())))
            .click();

        Map<String, Object> pathVariables = Map.of(
            "listItemType", type.getSelector()
        );

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().startsWith(UrlFactory.create(Endpoints.NOTEBOOK_NEW_LIST_ITEM_PAGE, pathVariables)))
            .assertTrue("New " + type.getSelector() + " page is not opened");
    }
}
