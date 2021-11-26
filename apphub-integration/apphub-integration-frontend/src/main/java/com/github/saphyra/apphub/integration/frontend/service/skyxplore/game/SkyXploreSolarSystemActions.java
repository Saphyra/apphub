package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFillContentEditable;

public class SkyXploreSolarSystemActions {
    public static boolean isOpened(WebDriver driver) {
        return GamePage.solarSystemPage(driver).isDisplayed();
    }

    public static WebElement getPlanet(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> getPlanets(driver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Planet not found."));
    }

    private static List<WebElement> getPlanets(WebDriver driver) {
        return GamePage.planets(driver);
    }

    public static String getSolarSystemName(WebDriver driver) {
        return GamePage.solarSystemName(driver).getText();
    }

    public static void renameSolarSystem(WebDriver driver, String newSolarSystemName) {
        clearAndFillContentEditable(driver, GamePage.solarSystemName(driver), newSolarSystemName);
        GamePage.solarSystemSvg(driver).click();
    }

    public static void closeSolarSystem(WebDriver driver) {
        GamePage.closeSolarSystemButton(driver).click();
    }
}
