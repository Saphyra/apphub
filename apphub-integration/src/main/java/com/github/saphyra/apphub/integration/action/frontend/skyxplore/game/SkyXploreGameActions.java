package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
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
        exitGameButton(driver)
            .click();

        driver.findElement(By.id("skyxplore-game-confirm-exit-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE));
    }

    public static void resumeGame(WebDriver driver) {
        driver.findElement(resumeGameButton())
            .click();
    }

    private static By resumeGameButton() {
        return By.id("skyxplore-game-resume-game-button");
    }

    public static void pauseGame(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-pause-game-button"))
            .click();
    }

    public static void saveAndExit(WebDriver driver) {
        exitGameButton(driver)
            .click();

        driver.findElement(By.id("skyxplore-game-confirm-save-and-exit-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE));
    }

    private static WebElement exitGameButton(WebDriver driver) {
        return driver.findElement(By.id("skyxplore-game-exit-button"));
    }

    public static Boolean isPlayerDisconnectedDialogOpened(WebDriver driver) {
        return WebElementUtils.isPresent(driver, By.id("skyxplore-game-player-disconnected-dialog"));
    }

    public static boolean isPaused(WebDriver driver) {
        return WebElementUtils.isPresent(driver, resumeGameButton());
    }

    public static Boolean isPlayerReconnectedDialogOpened(WebDriver driver) {
        return WebElementUtils.isPresent(driver, By.id("skyxplore-game-player-reconnected-dialog"));
    }

    public static boolean isPausedNotHost(WebDriver driver) {
        return WebElementUtils.isPresent(driver, By.id("skyxplore-game-paused"));
    }
}
