package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class PlanetStorageOverviewType {
    private final WebElement webElement;

    public int getReservedAmount() {
        return Integer.parseInt(webElement.findElement(By.className("skyxplore-game-planet-overview-storage-label-reserved")).getText());
    }

    public int getAvailable() {
        return Integer.parseInt(webElement.findElement(By.className("skyxplore-game-planet-overview-storage-label-actual")).getText());
    }
}
