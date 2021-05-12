package com.github.saphyra.apphub.integration.frontend.service.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu.SkyXploreMainMenuActions;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreLobbyCreationFlow {
    @SafeVarargs
    public static void setUpLobbyWithMembers(String gameName, WebDriver hostDriver, String hostName, BiWrapper<WebDriver, String>... members) {
        boolean allInMainMenu = Stream.concat(Stream.of(hostDriver), Arrays.stream(members).map(BiWrapper::getEntity1))
            .allMatch(driver -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE));
        assertThat(allInMainMenu).isTrue();

        Arrays.stream(members)
            .forEach(biWrapper -> SkyXploreFriendshipActions.setUpFriendship(hostDriver, biWrapper.getEntity1(), hostName, biWrapper.getEntity2()));

        SkyXploreMainMenuActions.createLobby(hostDriver, gameName);

        Arrays.stream(members)
            .peek(biWrapper -> SkyXploreLobbyActions.inviteFriend(hostDriver, biWrapper.getEntity2()))
            .parallel()
            .peek(biWrapper -> SkyXploreMainMenuActions.acceptInvitation(biWrapper.getEntity1(), hostName))
            .forEach(biWrapper -> AwaitilityWrapper.createDefault().until(() -> SkyXploreLobbyActions.pageLoaded(biWrapper.getEntity1())).assertTrue("Failed joining lobby"));
    }
}
