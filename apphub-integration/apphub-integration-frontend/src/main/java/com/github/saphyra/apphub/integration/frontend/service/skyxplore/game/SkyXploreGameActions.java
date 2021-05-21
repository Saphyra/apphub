package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class SkyXploreGameActions {
    public static boolean isGameLoaded(WebDriver driver) {
        return getSolarSystems(driver)
            .stream()
            .map(WebElement::getText).count() > 0;
    }

    private static List<WebElement> getSolarSystems(WebDriver driver) {
        return GamePage.solarSystems(driver);
    }
}
