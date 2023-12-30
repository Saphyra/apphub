package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SkyXploreSurfaceActions {
    public static boolean isDisplayed(WebDriver driver) {
        return GamePage.terraformationWindow(driver).isDisplayed();
    }

    public static void startTerraformation(WebDriver driver, String surfaceType) {
        driver.findElements(By.className("skyxplore-game-terraforming-possibility"))
            .stream()
            .filter(webElement -> webElement.getAttribute("id").equals("skyxplore-game-terraforming-possibility-" + surfaceType.toLowerCase()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TerraformingPossibility not found with surfaceType " + surfaceType))
            .findElement(By.className("skyxplore-game-terraform-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not loaded.");
    }
}
