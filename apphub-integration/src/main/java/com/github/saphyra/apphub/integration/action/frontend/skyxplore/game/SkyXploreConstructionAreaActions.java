package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.BuildingModule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SkyXploreConstructionAreaActions {
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
}
