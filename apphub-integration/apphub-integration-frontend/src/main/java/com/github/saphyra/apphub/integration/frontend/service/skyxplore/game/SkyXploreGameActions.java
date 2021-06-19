package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

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
}
