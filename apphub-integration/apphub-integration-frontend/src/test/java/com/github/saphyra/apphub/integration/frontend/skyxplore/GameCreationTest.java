package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyMember;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu.SkyXploreMainMenuActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameCreationTest extends SeleniumTest {
    @Test
    public void createGame() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData1);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);
        SkyXploreCharacterActions.submitForm(driver);

        SkyXploreMainMenuActions.waitForPageLoads(driver);

        SkyXploreMainMenuActions.openCreateGameDialog(driver);

        SkyXploreMainMenuActions.fillGameName(driver, "aa");
        SleepUtil.sleep(2000);
        SkyXploreMainMenuActions.verifyInvalidGameName(driver, "A játék neve túl rövid (3 karakter minimum).");

        SkyXploreMainMenuActions.fillGameName(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SleepUtil.sleep(2000);
        SkyXploreMainMenuActions.verifyInvalidGameName(driver, "A játék neve túl hosszú (30 karakter maximum).");

        SkyXploreMainMenuActions.fillGameName(driver, "game-name");
        SleepUtil.sleep(2000);
        SkyXploreMainMenuActions.verifyValidGameName(driver);

        SkyXploreMainMenuActions.submitGameCreationForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_LOBBY_PAGE))
            .assertTrue("SkyXplore Lobby page is not loaded.");

        SkyXploreLobbyActions.startGameCreation(driver);

        NotificationUtil.verifyErrorNotification(driver, "Várd meg, amíg mindenki kész van!");

        SkyXploreLobbyActions.setReady(driver);

        LobbyMember lobbyMember = SkyXploreLobbyActions.getHostMember(driver);
        AwaitilityWrapper.createDefault()
            .until(lobbyMember::isReady)
            .softAssertTrue();

        SkyXploreLobbyActions.startGameCreation(driver);
        AwaitilityWrapper.create(60, 1)
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_GAME_PAGE))
            .assertTrue("SkyXplore Game page is not loaded.");
    }
}
