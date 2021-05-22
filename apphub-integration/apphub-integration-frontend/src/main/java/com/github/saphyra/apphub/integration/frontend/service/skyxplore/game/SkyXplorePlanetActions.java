package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import org.openqa.selenium.WebDriver;

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
}
