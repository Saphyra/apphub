package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SkyXploreModifySurfaceActions {
    public static Boolean isDisplayed(WebDriver driver) {
        return WebElementUtils.isPresent(() -> driver.findElement(By.id("skyxplore-game-modify-surface")));
    }

    public static void constructBuilding(WebDriver driver, String dataId) {
        driver.findElements(By.className("skyxplore-game-available-building"))
            .stream()
            .filter(webElement -> webElement.getAttribute("id").replace("skyxplore-game-available-building-", "").equalsIgnoreCase(dataId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No building available with dataId " + dataId))
            .findElement(By.className("skyxplore-game-construct-new-building-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not loaded.");
    }

    public static void confirmUpgrade(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> GamePage.upgradeBuildingWindow(driver).isDisplayed())
            .assertTrue("UpgradeBuilding window is not displayed");

        GamePage.upgradeBuildingButton(driver)
            .click();
    }
}
