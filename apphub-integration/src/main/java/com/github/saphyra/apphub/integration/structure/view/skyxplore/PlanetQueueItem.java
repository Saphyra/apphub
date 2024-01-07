package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class PlanetQueueItem {
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.className("skyxplore-game-planet-queue-item-header"))
            .getText();
    }

    public void cancelItem(WebDriver driver) {
        webElement.findElement(By.className("skyxplore-game-planet-queue-item-cancel-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-planet-queue-item-cancel-button"))
            .click();
    }

    public double getStatus() {
        String stringValue = webElement.findElement(By.className("skyxplore-game-planet-queue-item-progress-bar-label-value"))
            .getText();
        return Double.parseDouble(stringValue);
    }
}
