package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.notebook.ListItem;
import com.github.saphyra.apphub.integration.structure.view.notebook.pin.PinGroup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PinActions {
    public static ListItem findPinnedItemByTitle(WebDriver driver, String title) {
        return getPinnedItems(driver)
            .stream()
            .filter(listItem -> listItem.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No pinned listItem found by title '" + title + "'"));
    }

    public static List<ListItem> getPinnedItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#notebook-pinned-items .notebook-content-category-content-list-item"))
            .stream()
            .map(ListItem::new)
            .collect(Collectors.toList());
    }

    public static void openPinGroupManager(WebDriver driver) {
        driver.findElement(By.id("notebook-manage-pin-groups-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("notebook-pin-group-manager")))
            .assertTrue("PinGroupManager is not opened.");
    }

    public static Optional<PinGroup> findPinGroupByName(WebDriver driver, String pinGroupName) {
        return getPinGroups(driver)
            .stream()
            .filter(pinGroup -> pinGroup.getName().equals(pinGroupName))
            .findAny();
    }

    public static List<PinGroup> getPinGroups(WebDriver driver) {
        return driver.findElements(By.cssSelector("#notebook-pin-groups .notebook-pin-group-wrapper"))
            .stream()
            .map(PinGroup::new)
            .collect(Collectors.toList());
    }

    public static PinGroup findPinGroupByNameValidated(WebDriver driver, String pinGroupName) {
        return findPinGroupByName(driver, pinGroupName)
            .orElseThrow(() -> new RuntimeException("PinGroup not found by name " + pinGroupName));
    }
}
