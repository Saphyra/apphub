package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.BuildingModule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SkyXploreConstructionAreaActions {
    public static void constructConstructionArea(WebDriver driver, String surfaceId, String dataId) {
        SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId)
            .openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructConstructionArea(driver, dataId);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isConstructionInProgress())
            .assertTrue("Construction is not started.");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(120, 5)
            .until(() -> !SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId).isConstructionInProgress())
            .assertTrue("Construction is not finished.");
        SkyXploreGameActions.pauseGame(driver);
    }

    public static void constructBuildingModule(WebDriver driver, String buildingModuleCategory, String dataId) {
        getBuildingModuleCategory(driver, buildingModuleCategory)
            .findElement(By.className("skyxplore-game-construction-area-construct-module-button"))
            .click();

        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.className("skyxplore-game-construct-building-module-available-building-" + dataId)))
            .orElseThrow(() -> new RuntimeException("No buildingModule available with dataId " + dataId))
            .findElement(By.className("skyxplore-game-construct-building-module-button"))
            .click();
    }

    private static WebElement getBuildingModuleCategory(WebDriver driver, String buildingModuleCategory) {
        return AwaitilityWrapper.getWithWait(() -> driver.findElement(By.className("skyxplore-game-construction-area-slots-" + buildingModuleCategory)))
            .orElseThrow();
    }

    public static List<BuildingModule> getBuildingModules(WebDriver driver, String buildingModuleCategory) {
        return getBuildingModuleCategory(driver, buildingModuleCategory)
            .findElements(By.cssSelector(".skyxplore-game-construction-area-slot:not(.skyxplore-game-construction-area-slot-empty)"))
            .stream()
            .map(BuildingModule::new)
            .collect(Collectors.toList());
    }

    @SafeVarargs
    public static void constructBuildingModules(WebDriver driver, BiWrapper<String, String>... buildingModules) {
        Arrays.stream(buildingModules)
            .forEach(buildingModule -> constructBuildingModule(driver, buildingModule.getEntity1(), buildingModule.getEntity2()));

        AwaitilityWrapper.createDefault()
            .until(() -> Arrays.stream(buildingModules).map(BiWrapper::getEntity1).allMatch(buildingModuleCategory -> getBuildingModules(driver, buildingModuleCategory).stream().anyMatch(BuildingModule::isConstructionInProgress)))
            .assertTrue("No construction started.");

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> Arrays.stream(buildingModules).map(BiWrapper::getEntity1).allMatch(buildingModuleCategory -> getBuildingModules(driver, buildingModuleCategory).stream().noneMatch(BuildingModule::isConstructionInProgress)))
            .assertTrue("BuildingModules are not constructed.");

        SkyXploreGameActions.pauseGame(driver);
    }

    public static void deconstructConstructionArea(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-construction-area-deconstruct-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-construction-area-deconstruction-confirm-button"))
            .click();
    }

    public static void close(WebDriver driver) {
        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("skyxplore-game-construction-area-close-button")))
            .orElseThrow(() -> new RuntimeException("Close constructionArea button not found"))
            .click();
    }
}
