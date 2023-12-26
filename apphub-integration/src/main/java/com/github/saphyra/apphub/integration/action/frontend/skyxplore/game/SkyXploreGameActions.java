package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SkyXploreGameActions {
    public static boolean isGameLoaded(WebDriver driver) {
        return SkyXploreMapActions.getSolarSystems(driver)
            .stream()
            .map(WebElement::getText)
            .findAny()
            .isPresent();
    }

    public static void exit(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-exit-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-confirm-exit-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE));
    }

    public static void resumeGame(WebDriver driver) {
        GamePage.resumeGameButton(driver).click();
    }

    public static void pauseGame(WebDriver driver) {
        GamePage.pauseGameButton(driver).click();
    }
}
