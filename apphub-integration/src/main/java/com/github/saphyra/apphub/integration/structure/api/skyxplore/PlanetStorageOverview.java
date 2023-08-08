package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

@RequiredArgsConstructor
public class PlanetStorageOverview {
    private final WebDriver webDriver;

    public PlanetStorageOverviewType getBulk() {
        return new PlanetStorageOverviewType(webDriver, "bulk");
    }
}
