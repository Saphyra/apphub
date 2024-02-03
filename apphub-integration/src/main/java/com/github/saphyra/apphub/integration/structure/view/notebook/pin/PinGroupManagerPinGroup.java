package com.github.saphyra.apphub.integration.structure.view.notebook.pin;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PinGroupManagerPinGroup {
    private final WebElement webElement;

    public String getName() {
        return webElement.findElement(By.className("notebook-pin-group-name"))
            .getText();
    }

    public void openRenameDialog(WebDriver driver) {
        webElement.findElement(By.className("notebook-pin-group-rename-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(driver, By.id("notebook-rename-pin-group-input")))
            .assertTrue("Rename pinGroup dialog not opened.");
    }

    public void expand() {
        if (!isExpanded()) {
            webElement.findElement(By.className("notebook-pin-group-display-items-toggle-button"))
                .click();

            AwaitilityWrapper.createDefault()
                .until(this::isExpanded)
                .assertTrue("PinGroupManagerPinGroup is not expanded.");
        }
    }

    private boolean isExpanded() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("notebook-pin-group-items")));
    }

    public List<PinGroupManagerPinGroupItem> getItems() {
        return webElement.findElements(By.className("notebook-pin-group-manager-pinned-item"))
            .stream()
            .map(PinGroupManagerPinGroupItem::new)
            .collect(Collectors.toList());
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("notebook-pin-group-delete-button"))
            .click();

        driver.findElement(By.id("notebook-delete-pin-group-button"))
            .click();
    }
}
