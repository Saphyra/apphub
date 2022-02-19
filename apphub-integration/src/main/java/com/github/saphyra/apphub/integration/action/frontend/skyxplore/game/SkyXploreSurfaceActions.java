package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SkyXploreSurfaceActions {
    public static boolean isDisplayed(WebDriver driver) {
        return GamePage.terraformationWindow(driver).isDisplayed();
    }

    public static void startTerraformation(WebDriver driver, String surfaceType) {
        GamePage.terraformingPossibilities(driver)
            .stream()
            .filter(webElement -> webElement.getAttribute("id").equals(surfaceType))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TerraformingPossibility not found with surfaceType " + surfaceType))
            .findElement(By.cssSelector(":scope .terraform-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not loaded.");
    }
}
