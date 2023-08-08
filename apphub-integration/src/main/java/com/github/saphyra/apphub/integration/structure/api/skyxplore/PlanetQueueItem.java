package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class PlanetQueueItem {
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.cssSelector(":scope .queue-item-title"))
            .getText();
    }

    public void cancelItem(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope .cancel-queue-item-button"))
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "cancel-queue-item-confirmation-dialog");
    }

    public double getStatus() {
        String stringValue = webElement.findElement(By.cssSelector(":scope .progress-bar-background"))
            .getCssValue("width")
            .split("%")
            [0]
            .split("px")
            [0];
        return Double.parseDouble(stringValue);
    }
}
