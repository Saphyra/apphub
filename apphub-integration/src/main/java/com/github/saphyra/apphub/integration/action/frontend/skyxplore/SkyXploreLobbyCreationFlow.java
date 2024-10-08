package com.github.saphyra.apphub.integration.action.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
public class SkyXploreLobbyCreationFlow {
    @SafeVarargs
    public static void setUpLobbyWithPlayers(WebDriver hostDriver, String hostName, BiWrapper<WebDriver, String>... players) {
        setUpLobbyWithPlayers(Constants.DEFAULT_GAME_NAME, hostDriver, hostName, players);
    }

    @SafeVarargs
    public static void setUpLobbyWithPlayers(String gameName, WebDriver hostDriver, String hostName, BiWrapper<WebDriver, String>... players) {
        log.debug("Setting up lobby...");

        Stream<WebDriver> playersStream = Stream.concat(Stream.of(hostDriver), Arrays.stream(players).map(BiWrapper::getEntity1));
        AwaitilityWrapper.createDefault()
            .until(() -> playersStream.allMatch(driver -> driver.getCurrentUrl().endsWith(SkyXploreDataEndpoints.SKYXPLORE_MAIN_MENU_PAGE)))
            .assertTrue("Not all players loaded the MainMenu page.");

        Arrays.stream(players)
            .forEach(biWrapper -> SkyXploreFriendshipActions.setUpFriendship(hostDriver, biWrapper.getEntity1(), hostName, biWrapper.getEntity2()));

        SkyXploreMainMenuActions.createLobby(hostDriver, gameName);

        Arrays.stream(players)
            .peek(biWrapper -> SkyXploreLobbyActions.inviteFriend(hostDriver, biWrapper.getEntity2()))
            .parallel()
            .peek(biWrapper -> SkyXploreMainMenuActions.acceptInvitation(biWrapper.getEntity1(), hostName))
            .forEach(biWrapper -> AwaitilityWrapper.createDefault().until(() -> SkyXploreLobbyActions.pageLoaded(biWrapper.getEntity1())).assertTrue("Failed joining lobby"));
    }
}
