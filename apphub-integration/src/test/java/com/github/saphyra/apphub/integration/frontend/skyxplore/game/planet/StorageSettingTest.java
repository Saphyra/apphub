package com.github.saphyra.apphub.integration.frontend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXplorePlanetStorageSettingActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.lobby.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.StorageSetting;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StorageSettingTest extends SeleniumTest {
    public static final int AMOUNT = 10;

    @Test(groups = {"fe", "skyxplore"})
    public void storageSettingCrud() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(driver, registrationParameters.getUsername());
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

        StorageSetting storageSetting = createStorageSettingForCrud(driver);
        StorageSetting editedSetting = editStorageSetting(driver, storageSetting);
        deleteStorageSetting(driver, editedSetting);
    }

    private static StorageSetting createStorageSettingForCrud(WebDriver driver) {
        SkyXplorePlanetStorageSettingActions.create(driver, Constants.DATA_ID_ORE, 10, 3);

        StorageSetting storageSetting = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver), storageSettings -> storageSettings.size() == 2)
            .stream()
            .filter(ss -> ss.getDataId().equals(Constants.DATA_ID_ORE))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Storage Setting not found."));

        assertThat(storageSetting.getAmount()).isEqualTo(10);
        assertThat(storageSetting.getPriority()).isEqualTo(3);

        assertThat(SkyXplorePlanetStorageSettingActions.createStorageSettingResourceSelectMenu(driver).getOptions()).doesNotContain(Constants.DATA_ID_ORE);

        return storageSetting;
    }

    private static StorageSetting editStorageSetting(WebDriver driver, StorageSetting storageSetting) {
        storageSetting.setAmount(20);
        storageSetting.setPriority(8);
        storageSetting.saveChanges(driver);

        StorageSetting editedSetting = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver), storageSettings -> storageSettings.size() == 2)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Storage Setting not found."));
        assertThat(editedSetting.getResourceName()).isEqualTo("Ore");
        assertThat(editedSetting.getAmount()).isEqualTo(20);
        assertThat(editedSetting.getPriority()).isEqualTo(8);
        return editedSetting;
    }

    private static void deleteStorageSetting(WebDriver driver, StorageSetting editedSetting) {
        editedSetting.delete(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver).size() == 1)
            .assertTrue("StorageSetting not deleted.");

        assertThat(SkyXplorePlanetStorageSettingActions.createStorageSettingResourceSelectMenu(driver).getOptions()).contains(Constants.DATA_ID_ORE);
    }

    @Test(groups = {"fe", "skyxplore"})
    public void produceResourcesForStorageSetting() {
        WebDriver driver = extractDriver();
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, registrationParameters);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.createCharacter(driver);
        SkyXploreLobbyCreationFlow.setUpLobbyWithPlayers(driver, registrationParameters.getUsername());
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

        createStorageSetting(driver);
        checkStorageReserved(driver);
        checkResourceProduced(driver);
    }

    private static void createStorageSetting(WebDriver driver) {
        SkyXplorePlanetStorageSettingActions.create(driver, Constants.DATA_ID_ORE, AMOUNT, 3);

        StorageSetting storageSetting = AwaitilityWrapper.getListWithWait(() -> SkyXplorePlanetStorageSettingActions.getStorageSettings(driver), storageSettings -> !storageSettings.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Storage Setting not found."));
        assertThat(storageSetting.getResourceName()).isEqualTo("Ore");

        SkyXplorePlanetActions.closeStorageSettingsWindow(driver);

        SkyXploreGameActions.resumeGame(driver);
    }

    private static void checkStorageReserved(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getStorageOverview(driver).getBulk().getReservedAmount() > 0)
            .assertTrue("Storage not reserved.");
    }

    private static void checkResourceProduced(WebDriver driver) {
        AwaitilityWrapper.create(60, 10)
            .until(() -> SkyXplorePlanetActions.getStorageOverview(driver).getBulk().getAvailable() == AMOUNT + 100)
            .assertTrue("Resource not produced.");
    }
}
