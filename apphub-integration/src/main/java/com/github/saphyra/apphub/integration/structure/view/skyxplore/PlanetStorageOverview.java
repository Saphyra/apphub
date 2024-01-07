package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetStorageOverviewType;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor
public class PlanetStorageOverview {
    private final WebDriver driver;

    public PlanetStorageOverviewType getBulk() {
        return new PlanetStorageOverviewType(driver.findElement(By.id("skyxplore-game-planet-overview-storage-bulk")));
    }
}
