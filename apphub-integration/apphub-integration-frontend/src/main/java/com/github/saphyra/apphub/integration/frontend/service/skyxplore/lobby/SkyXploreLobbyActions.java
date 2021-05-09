package com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby;

import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyMember;
import org.openqa.selenium.WebDriver;

public class SkyXploreLobbyActions {
    public static void startGameCreation(WebDriver driver) {
        LobbyPage.startGameCreationButton(driver).click();
    }

    public static void setReady(WebDriver driver) {
        LobbyPage.setReadyButton(driver).click();
    }

    public static LobbyMember getHostMember(WebDriver driver) {
        return new LobbyMember(LobbyPage.hostMember(driver));
    }
}
