package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SkyXploreGameActions {
    public static boolean isGameLoaded(WebDriver driver) {
        return SkyXploreMapActions.getSolarSystems(driver)
            .stream()
            .map(WebElement::getText).count() > 0;
    }

    public static void exit(WebDriver driver) {
        GamePage.exitButton(driver).click();

        CommonPageActions.confirmConfirmationDialog(driver, "exit-game-confirmation-dialog");

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
