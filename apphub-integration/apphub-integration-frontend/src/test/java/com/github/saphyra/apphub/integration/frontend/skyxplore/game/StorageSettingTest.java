package com.github.saphyra.apphub.integration.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.StorageSetting;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXplorePlanetStorageSettingActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StorageSettingTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";
    public static final String RESOURCE_ID = "ore";

    @Test
    public void storageSettingCrud() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver, registrationParameters.getUsername());
        SkyXploreLobbyActions.setReady(driver);
        SkyXploreLobbyActions.startGameCreation(driver);

        AwaitilityWrapper.create(60, 1)
            .until(() -> SkyXploreGameActions.isGameLoaded(driver))
            .assertTrue("Game not loaded.");

        SkyXploreMapActions.getSolarSystem(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSolarSystemActions.isOpened(driver))
            .assertTrue("SolarSystem is not opened.");

        SkyXploreSolarSystemActions.getPlanet(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.isLoaded(driver))
            .assertTrue("Planet is not opened.");

        SkyXplorePlanetActions.openStorageSettingWindow(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetStorageSettingActions.isLoaded(driver))
            .assertTrue("StorageSettings is not opened.");

        //Create storage setting
        SkyXplorePlanetStorageSettingActions.create(driver, RESOURCE_ID, 10, 5, 3);

        assertThat(SkyXplorePlanetStorageSettingActions.createStorageSettingResourceSelectMenu(driver).getOptions()).doesNotContain(RESOURCE_ID);
        StorageSetting storageSetting = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver), storageSettings -> !storageSettings.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Storage Setting not found."));
        assertThat(storageSetting.getResourceName()).isEqualTo("Érc");
        assertThat(storageSetting.getAmount()).isEqualTo(10);
        assertThat(storageSetting.getBatchSize()).isEqualTo(5);
        assertThat(storageSetting.getPriority()).isEqualTo(3);

        //Edit storage setting
        storageSetting.setAmount(20);
        storageSetting.setBatchSize(7);
        storageSetting.setPriority(8);
        storageSetting.saveChanges(driver);

        StorageSetting editedSetting = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver), storageSettings -> !storageSettings.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Storage Setting not found."));
        assertThat(editedSetting.getResourceName()).isEqualTo("Érc");
        assertThat(editedSetting.getAmount()).isEqualTo(20);
        assertThat(editedSetting.getBatchSize()).isEqualTo(7);
        assertThat(editedSetting.getPriority()).isEqualTo(8);

        //Delete storage setting
        editedSetting.delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver).isEmpty())
            .assertTrue("StorageSetting not deleted.");

        assertThat(SkyXplorePlanetStorageSettingActions.createStorageSettingResourceSelectMenu(driver).getOptions()).contains(RESOURCE_ID);
    }
}
