package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOption;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.GameSettingOptionValue;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.LobbyMember;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.SkyXploreLobbyCreationFlow;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.lobby.SkyXploreLobbyActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class LobbySettingsTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test
    public void modifySettings() {
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

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        Arrays.stream(GameSettingOption.values())
            .forEach(option -> changeAndVerify(driver1, driver2, option));
    }

    private void changeAndVerify(WebDriver hostDriver, WebDriver memberDriver, GameSettingOption option) {
        List<GameSettingOptionValue> possibleOptions = option.getPossibleOptions();
        Stream.of(possibleOptions.get(0), possibleOptions.get(possibleOptions.size() - 1))
            .forEach(optionValue -> {
                SkyXploreLobbyActions.changeGameSetting(hostDriver, option, optionValue);
                Stream.of(hostDriver, memberDriver)
                    .forEach(driver -> verify(driver, option, optionValue));
            });
    }

    private void verify(WebDriver driver, GameSettingOption option, GameSettingOptionValue optionValue) {
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getGameSettingInput(driver, option).getValue().equals(optionValue.name()))
            .softAssertTrue();
    }

    @Test
    public void alliances() {
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

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        LobbyMember hostMember = SkyXploreLobbyActions.getHostMember(driver1);
        hostMember.changeAllianceTo(Constants.NEW_ALLIANCE_VALUE);
        AwaitilityWrapper.createDefault()
            .until(() -> hostMember.getAlliance().equals("1"))
            .softAssertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getHostMember(driver2).getAlliance().equals("1"))
            .softAssertTrue();

        getSoftAssertions().assertThat(SkyXploreLobbyActions.getHostMember(driver2).allianceChangeEnabled()).isFalse();

        LobbyMember lobbyMember = SkyXploreLobbyActions.getMember(driver2, userData2.getUsername());
        lobbyMember.changeAllianceTo("1");

        AwaitilityWrapper.createDefault()
            .until(() -> lobbyMember.getAlliance().equals("1"))
            .softAssertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver1, userData2.getUsername()).getAlliance().equals("1"))
            .softAssertTrue();

        SkyXploreLobbyActions.getMember(driver1, userData2.getUsername())
            .changeAllianceTo(Constants.NO_ALLIANCE_VALUE);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver1, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_VALUE))
            .softAssertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver2, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_VALUE))
            .softAssertTrue();
    }
}
