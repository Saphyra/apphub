package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.notebook.pin.PinGroupManagerPinGroup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PinGroupManagerActions {
    public static void createPinGroupWithName(WebDriver driver, String pinGroupName) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-pin-group-title")), pinGroupName);

        driver.findElement(By.id("notebook-new-pin-group-create-button"))
            .click();
    }

    public static Optional<PinGroupManagerPinGroup> findPinGroupByName(WebDriver driver, String pinGroupName) {
        return getPinGroups(driver)
            .stream()
            .filter(pinGroupManagerPinGroup -> pinGroupManagerPinGroup.getName().equals(pinGroupName))
            .findAny();
    }

    public static List<PinGroupManagerPinGroup> getPinGroups(WebDriver driver) {
        return driver.findElements(By.cssSelector("#notebook-pin-group-manager-groups .notebook-pin-group"))
            .stream()
            .map(PinGroupManagerPinGroup::new)
            .collect(Collectors.toList());
    }

    public static PinGroupManagerPinGroup findPinGroupByNameValidated(WebDriver driver, String pinGroupName) {
        return findPinGroupByName(driver, pinGroupName)
            .orElseThrow(() -> new RuntimeException("PinGroupManagerPinGroup not found by name " + pinGroupName));
    }

    public static void renamePinGroup(WebDriver driver, String newPinGroupName) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-rename-pin-group-input")), newPinGroupName);

        driver.findElement(By.id("notebook-pin-group-rename-button"))
            .click();
    }
}
