package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SkyXploreConstructionActions {
    public static Boolean isDisplayed(WebDriver driver) {
        return GamePage.constructionWindow(driver)
            .isDisplayed();
    }

    public static void constructBuilding(WebDriver driver, String dataId) {
        GamePage.availableBuildings(driver)
            .stream()
            .filter(webElement -> webElement.getAttribute("id").equalsIgnoreCase(dataId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No building available with dataId " + dataId))
            .findElement(By.cssSelector(":scope .construct-new-building-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not loaded.");
    }
}
