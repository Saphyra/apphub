package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

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
        AwaitilityWrapper.getOptionalWithWait(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("skyxplore-game-upgrade-building-button"))), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Upgrade building button is not displayed."))
            .click();
    }

    public static void startTerraformation(WebDriver driver, String surfaceType) {
        AwaitilityWrapper.getOptionalWithWait(() -> findTerraformingPossibility(driver, surfaceType))
            .orElseThrow(() -> new RuntimeException("TerraformingPossibility not found with surfaceType " + surfaceType))
            .findElement(By.className("skyxplore-game-terraform-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not loaded.");
    }

    private static Optional<WebElement> findTerraformingPossibility(WebDriver driver, String surfaceType) {
        return driver.findElements(By.className("skyxplore-game-terraforming-possibility"))
            .stream()
            .filter(webElement -> webElement.getAttribute("id").equals("skyxplore-game-terraforming-possibility-" + surfaceType.toLowerCase()))
            .findFirst();
    }
}
