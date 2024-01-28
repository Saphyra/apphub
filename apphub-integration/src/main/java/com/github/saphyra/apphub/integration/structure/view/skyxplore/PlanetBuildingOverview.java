package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class PlanetBuildingOverview {
    private final WebDriver driver;

    public PlanetBuildingOverviewItem getForSurfaceType(String surfaceType) {
        WebElement webElement = WebElementUtils.getIfPresent(() -> driver.findElement(By.id("skyxplore-game-planet-overview-building-" + surfaceType.toLowerCase())))
            .orElse(null);
        return new PlanetBuildingOverviewItem(webElement);
    }
}
