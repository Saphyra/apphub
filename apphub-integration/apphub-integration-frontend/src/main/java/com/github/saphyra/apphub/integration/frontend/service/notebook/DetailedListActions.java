package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class DetailedListActions {
    public static String getTitleOfOpenedCategory(WebDriver driver) {
        return NotebookPage.titleOfOpenedCategory(driver).getText();
    }

    public static List<ListItemDetailsItem> getDetailedListItems(WebDriver driver) {
        return NotebookPage.detailedListItems(driver)
            .stream()
            .map(ListItemDetailsItem::new)
            .collect(Collectors.toList());
    }

    public static ListItemDetailsItem findDetailedItem(WebDriver driver, String title) {
        return AwaitilityWrapper.getListWithWait(() -> getDetailedListItems(driver), listItemDetailsItems -> !listItemDetailsItems.isEmpty())
            .stream()
            .filter(listItemDetailsItem -> listItemDetailsItem.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("listItem not found with title " + title));
    }
}
