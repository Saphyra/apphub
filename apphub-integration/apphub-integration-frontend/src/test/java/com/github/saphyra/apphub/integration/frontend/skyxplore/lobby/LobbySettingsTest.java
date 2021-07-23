package com.github.saphyra.apphub.integration.frontend.skyxplore.lobby;

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LobbySettingsTest extends SeleniumTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = "skyxplore")
    public void setUpLobby() {
        WebDriver driver1 = extractDriver();
        WebDriver driver2 = extractDriver();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData1 = RegistrationParameters.validParameters();

        List<Future<Object>> futures = Stream.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2))
            .map(player -> EXECUTOR_SERVICE.submit(() -> {
                Navigation.toIndexPage(player.getEntity1());
                IndexPageActions.registerUser(player.getEntity1(), player.getEntity2());
                ModulesPageActions.openModule(player.getEntity1(), ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(player.getEntity1());
                new WebDriverWait(player.getEntity1(), Duration.ofSeconds(10))
                    .until(ExpectedConditions.textToBe(By.id("main-title"), "Főmenü"));
                return null;
            }))
            .collect(Collectors.toList());


        AwaitilityWrapper.create(120, 5)
            .until(() -> futures.stream().allMatch(Future::isDone))
            .assertTrue("Players not created.");

        SkyXploreLobbyCreationFlow.setUpLobbyWithMembers(GAME_NAME, driver1, userData1.getUsername(), new BiWrapper<>(driver2, userData2.getUsername()));

        //Lobby settings
        Arrays.stream(GameSettingOption.values())
            .forEach(option -> changeAndVerify(driver1, driver2, option));

        //Alliance settings
        LobbyMember hostMember = SkyXploreLobbyActions.getHostMember(driver1);
        hostMember.changeAllianceTo(Constants.NEW_ALLIANCE_VALUE);
        AwaitilityWrapper.createDefault()
            .until(() -> hostMember.getAlliance().equals("1"))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getHostMember(driver2).getAlliance().equals("1"))
            .assertTrue();

        assertThat(SkyXploreLobbyActions.getHostMember(driver2).allianceChangeEnabled()).isFalse();

        LobbyMember lobbyMember = SkyXploreLobbyActions.getMember(driver2, userData2.getUsername());
        lobbyMember.changeAllianceTo("1");

        AwaitilityWrapper.createDefault()
            .until(() -> lobbyMember.getAlliance().equals("1"))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver1, userData2.getUsername()).getAlliance().equals("1"))
            .assertTrue();

        SkyXploreLobbyActions.getMember(driver1, userData2.getUsername())
            .changeAllianceTo(Constants.NO_ALLIANCE_VALUE);

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver1, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_VALUE))
            .assertTrue();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreLobbyActions.getMember(driver2, userData2.getUsername()).getAlliance().equals(Constants.NO_ALLIANCE_VALUE))
            .assertTrue();
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
            .assertTrue();
    }
}
