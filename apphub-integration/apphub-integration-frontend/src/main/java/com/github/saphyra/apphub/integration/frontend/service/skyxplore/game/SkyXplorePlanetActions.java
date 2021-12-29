package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Surface;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFillContentEditable;

public class SkyXplorePlanetActions {
    public static boolean isLoaded(WebDriver driver) {
        return GamePage.planetPage(driver).isDisplayed();
    }

    public static void openStorageSettingWindow(WebDriver driver) {
        GamePage.openStorageSettingButton(driver).click();
    }

    public static void openPopulationOverview(WebDriver driver) {
        GamePage.openCitizenOverviewButton(driver).click();
    }

    public static String getPlanetName(WebDriver driver) {
        return GamePage.planetName(driver).getText();
    }

    public static void renamePlanet(WebDriver driver, String newPlanetName) {
        clearAndFillContentEditable(driver, GamePage.planetName(driver), newPlanetName);
        GamePage.planetMiddleBar(driver).click();
    }

    public static void closePlanet(WebDriver driver) {
        GamePage.closePlanetButton(driver).click();
    }

    public static Surface findEmptySurface(WebDriver driver, String surfaceType) {
        return GamePage.surfacesOfPlanet(driver)
            .stream()
            .map(Surface::new)
            .filter(surface -> surface.getSurfaceType().equalsIgnoreCase(surfaceType))
            .filter(Surface::isEmpty)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Empty surface not found with type " + surfaceType));
    }

    public static Surface findBySurfaceId(WebDriver driver, String surfaceId) {
        return GamePage.surfacesOfPlanet(driver)
            .stream()
            .map(Surface::new)
            .filter(surface -> surface.getSurfaceId().equals(surfaceId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Surface not found with id " + surfaceId));
    }

    public static Surface findSurfaceWithUpgradableBuilding(WebDriver driver) {
        return GamePage.surfacesOfPlanet(driver)
            .stream()
            .map(Surface::new)
            .filter(Surface::canUpgradeBuilding)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No surface with upgradable building"));
    }
}
