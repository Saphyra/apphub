package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CharacterCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "skyxplore"})
    public void createAndEditCharacter() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData1);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        assertThat(SkyXploreCharacterActions.getBoxTitle(driver)).isEqualTo("New character");
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(userData1.getUsername());

        SkyXploreCharacterActions.fillCharacterName(driver, "aa");
        SkyXploreCharacterActions.verifyInvalidCharacterName(driver, "Character name too short. (Minimum 3 characters)");

        SkyXploreCharacterActions.fillCharacterName(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreCharacterActions.verifyInvalidCharacterName(driver, "Character name too long. (Maximum 30 characters)");

        SkyXploreCharacterActions.fillCharacterName(driver, userData1.getUsername());
        SkyXploreCharacterActions.verifyValidCharacterName(driver);

        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.SKYXPLORE_CHARACTER_SAVED);

        SkyXploreMainMenuActions.back(driver);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData2);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.fillCharacterName(driver, userData1.getUsername());
        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.SKYXPLORE_CHARACTER_NAME_ALREADY_EXISTS);

        SkyXploreCharacterActions.fillCharacterName(driver, userData2.getUsername());
        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.SKYXPLORE_CHARACTER_SAVED);

        SkyXploreMainMenuActions.editCharacter(driver);

        assertThat(SkyXploreCharacterActions.getBoxTitle(driver)).isEqualTo("Edit character");
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(userData2.getUsername());

        String newCharacterName = RegistrationParameters.validParameters()
            .getUsername();
        SkyXploreCharacterActions.fillCharacterName(driver, newCharacterName);
        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.SKYXPLORE_CHARACTER_SAVED);

        SkyXploreMainMenuActions.editCharacter(driver);
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(newCharacterName);
    }
}
