package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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

    public static void up(WebDriver driver) {
        WebElement upButton = NotebookPage.detailedListUpButton(driver);
        assertThat(upButton.isEnabled()).isTrue();

        upButton.click();
    }
}
