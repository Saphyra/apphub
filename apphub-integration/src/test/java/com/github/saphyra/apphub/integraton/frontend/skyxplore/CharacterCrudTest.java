package com.github.saphyra.apphub.integraton.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu.SkyXploreMainMenuActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CharacterCrudTest extends SeleniumTest {
    @Test(groups = "skyxplore")
    public void createAndEditCharacter() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData1);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        assertThat(SkyXploreCharacterActions.getBoxTitle(driver)).isEqualTo("Új karakter");
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(userData1.getUsername());

        SkyXploreCharacterActions.fillCharacterName(driver, "aa");
        SkyXploreCharacterActions.verifyInvalidCharacterName(driver, "Karakter név túl rövid. (Minimum 3 karakter)");

        SkyXploreCharacterActions.fillCharacterName(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SkyXploreCharacterActions.verifyInvalidCharacterName(driver, "Karakter név túl hosszú. (Maximum 30 karakter)");

        SkyXploreCharacterActions.fillCharacterName(driver, userData1.getUsername());
        SkyXploreCharacterActions.verifyValidCharacterName(driver);

        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifySuccessToast(driver, "Karakter elmentve.");

        SkyXploreMainMenuActions.back(driver);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData2);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.fillCharacterName(driver, userData1.getUsername());
        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifyErrorToast(driver, "Ez a karakternév már foglalt.");

        SkyXploreCharacterActions.fillCharacterName(driver, userData2.getUsername());
        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifySuccessToast(driver, "Karakter elmentve.");

        SkyXploreMainMenuActions.editCharacter(driver);

        assertThat(SkyXploreCharacterActions.getBoxTitle(driver)).isEqualTo("Karakter szerkesztése");
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(userData2.getUsername());

        String newCharacterName = RegistrationParameters.validParameters()
            .getUsername();
        SkyXploreCharacterActions.fillCharacterName(driver, newCharacterName);
        SkyXploreCharacterActions.submitForm(driver);

        ToastMessageUtil.verifySuccessToast(driver, "Karakter elmentve.");

        SkyXploreMainMenuActions.editCharacter(driver);
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(newCharacterName);
    }
}
