package com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.api.notebook.PinnedItem;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class PinnedItemActions {
    public static List<PinnedItem> getPinnedItems(WebDriver driver) {
        return NotebookPage.pinnedItems(driver)
            .stream()
            .map(PinnedItem::new)
            .collect(Collectors.toList());
    }

    public static PinnedItem findPinnedItem(WebDriver driver, String title) {
        return AwaitilityWrapper.getListWithWait(() -> getPinnedItems(driver), pinnedItems -> pinnedItems.stream().anyMatch(pinnedItem -> pinnedItem.getTitle().equals(title)))
            .stream()
            .filter(pinnedItem -> pinnedItem.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Pinned item not found."));
    }
}
