package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor
public class PlanetStorageOverviewType {
    private final WebDriver driver;
    private final String storageType;

    public int getReservedAmount() {
        return Integer.parseInt(driver.findElement(By.id(String.format("planet-storage-%s-reserved-value", storageType))).getText());
    }

    public int getAvailable() {
        return Integer.parseInt(driver.findElement(By.id(String.format("planet-storage-%s-actual-value", storageType))).getText());
    }
}
