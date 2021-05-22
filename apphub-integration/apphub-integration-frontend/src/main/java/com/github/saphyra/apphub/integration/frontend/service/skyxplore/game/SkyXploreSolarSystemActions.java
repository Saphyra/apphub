package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SkyXploreSolarSystemActions {
    public static boolean isOpened(WebDriver driver) {
        return GamePage.solarSystemPage(driver).isDisplayed();
    }

    public static WebElement getPlanet(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> getPlanets(driver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet not found."));
    }

    private static List<WebElement> getPlanets(WebDriver driver) {
        return GamePage.planets(driver);
    }
}
