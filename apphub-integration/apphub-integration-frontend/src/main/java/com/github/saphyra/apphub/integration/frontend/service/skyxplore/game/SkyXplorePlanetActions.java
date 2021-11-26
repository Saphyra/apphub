package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

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
}
