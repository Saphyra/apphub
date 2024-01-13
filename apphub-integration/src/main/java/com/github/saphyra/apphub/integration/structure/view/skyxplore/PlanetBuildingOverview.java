package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor
public class PlanetBuildingOverview {
    private final WebDriver driver;

    public PlanetBuildingOverviewItem getForSurfaceType(String surfaceType) {
        return new PlanetBuildingOverviewItem(driver.findElement(By.id("skyxplore-game-planet-overview-building-" + surfaceType.toLowerCase())));
    }
}
