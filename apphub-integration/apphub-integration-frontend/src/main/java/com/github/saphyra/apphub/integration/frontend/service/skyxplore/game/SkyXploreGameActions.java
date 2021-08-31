package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.frontend.service.common.CommonPageActions;
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
}
