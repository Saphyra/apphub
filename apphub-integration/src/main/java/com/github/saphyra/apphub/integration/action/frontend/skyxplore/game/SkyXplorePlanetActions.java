package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetBuildingOverview;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetQueueItem;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.PlanetStorageOverview;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class SkyXplorePlanetActions {
    public static boolean isLoaded(WebDriver driver) {
        return WebElementUtils.isPresent(() -> driver.findElement(By.id("skyxplore-game-planet")));
    }

    public static void openStorageSettingWindow(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-planet-overview-open-storage-button"))
            .click();
    }

    public static void closeStorageSettingsWindow(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-storage-close-button"))
            .click();
    }

    public static void openPopulationOverview(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-planet-overview-population-details-button"))
            .click();
    }

    public static String getPlanetName(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-game-planet-name"))
            .getText();
    }

    public static void renamePlanet(WebDriver driver, String newPlanetName) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("skyxplore-game-planet-name-edit-input")), newPlanetName);
    }

    public static void closePlanet(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-planet-close-button"))
            .click();
    }

    public static Surface findEmptySurface(WebDriver driver, String surfaceType) {
        return getSurfaces(driver)
            .stream()
            .filter(surface -> surface.getSurfaceType().equalsIgnoreCase(surfaceType))
            .filter(Surface::isEmpty)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Empty surface not found with type " + surfaceType));
    }

    public static Surface findBySurfaceIdValidated(WebDriver driver, String surfaceId) {
        return findBySurfaceId(driver, surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found with id " + surfaceId));
    }

    public static Optional<Surface> findBySurfaceId(WebDriver driver, String surfaceId) {
        return getSurfaces(driver)
            .stream()
            .filter(surface -> surface.getSurfaceId().equals(surfaceId))
            .findFirst();
    }

    private static List<Surface> getSurfaces(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-game-planet-surface-tile"))
            .stream()
            .map(Surface::new)
            .collect(Collectors.toList());
    }

    public static Surface findSurfaceWithUpgradableBuilding(WebDriver driver) {
        return getSurfaces(driver)
            .stream()
            .filter(Surface::canUpgradeBuilding)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No surface with upgradable building"));
    }

    public static List<PlanetQueueItem> getQueue(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-game-planet-queue-item"))
            .stream()
            .map(PlanetQueueItem::new)
            .collect(Collectors.toList());
    }

    public static PlanetStorageOverview getStorageOverview(WebDriver driver) {
        return new PlanetStorageOverview(driver);
    }

    public static Surface findSurfaceWithBuilding(WebDriver driver, String dataId) {
        return getSurfaces(driver)
            .stream()
            .filter(surface -> surface.getBuildingDataId().filter(s -> s.equals(dataId)).isPresent())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No surface found with building " + dataId));
    }

    public static void enableNameEditing(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-planet-name-edit-button"))
            .click();
    }

    public static void saveNewPlanetName(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-planet-name-save-button"))
            .click();
    }

    public static void discardNewPlanetName(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-planet-name-discard-button"))
            .click();
    }

    public static PlanetBuildingOverview getBuildingOverview(WebDriver driver) {
        return new PlanetBuildingOverview(driver);
    }

    public static void toggleBuildings(WebDriver driver) {
        driver.findElement(By.cssSelector("#skyxplore-game-planet-overview-building .skyxplore-game-planet-overview-tab-expand-button"))
            .click();
    }
}
