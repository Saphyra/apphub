package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


public class SkyXploreSolarSystemActions {
    public static boolean isOpened(WebDriver driver) {
        return WebElementUtils.isPresent(() -> driver.findElement(By.id("skyxplore-game-solar-system")));
    }

    public static WebElement getPlanet(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> getPlanets(driver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet not found."));
    }

    private static List<WebElement> getPlanets(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-game-solar-system-planet"));
    }

    public static String getSolarSystemName(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-game-solar-system-name")).getText();
    }

    public static void renameSolarSystem(WebDriver driver, String newSolarSystemName) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("skyxplore-game-solar-system-name-edit-input")), newSolarSystemName);
    }

    public static void saveNewSolarSystemName(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-solar-system-name-save-button")).click();
    }

    public static void closeSolarSystem(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-solar-system-close-button"))
            .click();
    }

    public static void enableNameEditing(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-solar-system-name-edit-button"))
            .click();
    }

    public static void discardNewSolarSystemName(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-solar-system-name-discard-button"))
            .click();
    }
}
