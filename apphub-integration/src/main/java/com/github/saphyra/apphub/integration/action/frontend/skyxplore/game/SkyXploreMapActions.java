package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SkyXploreMapActions {
    public static List<WebElement> getSolarSystems(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-game-map-solar-system"));
    }

    public static WebElement getSolarSystem(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> getSolarSystems(driver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No SolarSystem found."));
    }
}
