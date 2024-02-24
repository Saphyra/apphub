package com.github.saphyra.apphub.integration.structure.view.notebook.pin;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class PinGroupManagerPinGroupItem {
    private final WebElement webElement;

    public void remove() {
        webElement.findElement(By.className("notebook-pin-group-manager-pinned-item-remove-button"))
            .click();
    }
}
