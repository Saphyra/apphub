package com.github.saphyra.apphub.integraton.frontend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreFriendshipActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbyInvitationTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = "skyxplore")
    public void inviteFriendToLobby() {
        WebDriver driver1 = extractDriver();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        WebDriver driver2 = extractDriver();
        Future<Void> driverFuture = EXECUTOR_SERVICE.submit(() -> {
            Navigation.toIndexPage(driver2);
            IndexPageActions.registerUser(driver2, userData2);
            ModulesPageActions.openModule(driver2, ModuleLocation.SKYXPLORE);
            SkyXploreCharacterActions.submitForm(driver2);
            return null;
        });

        Navigation.toIndexPage(driver1);
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver1, userData1);

        ModulesPageActions.openModule(driver1, ModuleLocation.SKYXPLORE);
        SkyXploreCharacterActions.submitForm(driver1);

        AwaitilityWrapper.create(120, 5)
            .until(driverFuture::isDone)
            .assertTrue("Member player is not created.");

        SkyXploreFriendshipActions.setUpFriendship(driver1, driver2, userData1.getUsername(), userData2.getUsername());

        SkyXploreMainMenuActions.back(driver2);

        SkyXploreMainMenuActions.createLobby(driver1, GAME_NAME);

        assertThat(SkyXploreLobbyActions.getOnlineFriends(driver1)).isEmpty();

        ModulesPageActions.openModule(driver2, ModuleLocation.SKYXPLORE);

        WebElement inviteFriendButton = SkyXploreLobbyActions.getOnlineFriend(driver1, userData2.getUsername());

        inviteFriendButton.click();
        NotificationUtil.verifySuccessNotification(driver1, "Barát meghívva.");

        inviteFriendButton.click();
        NotificationUtil.verifyErrorNotification(driver1, "Ezt a játékost nem rég hívtad meg. Várj pár másodpercet, mielőtt újra próbálkozhatsz!");

        SkyXploreMainMenuActions.acceptInvitation(driver2, userData1.getUsername());

        AwaitilityWrapper.createDefault()
            .until(() -> driver2.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_LOBBY_PAGE))
            .assertTrue("Invited member was not redirected to lobby.");

        AwaitilityWrapper.getListWithWait(() -> SkyXploreLobbyActions.getMembers(driver1), lobbyMembers -> !lobbyMembers.isEmpty())
            .stream()
            .filter(member -> member.getName().equals(userData2.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Lobby member not found."));
    }
}
