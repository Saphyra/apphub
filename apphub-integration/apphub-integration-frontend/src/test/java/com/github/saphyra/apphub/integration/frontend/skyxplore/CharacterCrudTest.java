package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu.SkyXploreMainMenuActions;
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
        SleepUtil.sleep(2000);
        SkyXploreCharacterActions.verifyInvalidCharacterName(driver, "Karakternév túl rövid (minimum 3 karakter).");

        SkyXploreCharacterActions.fillCharacterName(driver, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));
        SleepUtil.sleep(2000);
        SkyXploreCharacterActions.verifyInvalidCharacterName(driver, "Karakternév túl hosszú (maximum 30 karakter).");

        SkyXploreCharacterActions.fillCharacterName(driver, userData1.getUsername());
        SleepUtil.sleep(2000);
        SkyXploreCharacterActions.verifyValidCharacterName(driver);

        SkyXploreCharacterActions.submitForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Karakter elmentve.");

        SkyXploreMainMenuActions.back(driver);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData2);

        ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);

        SkyXploreCharacterActions.fillCharacterName(driver, userData1.getUsername());
        SleepUtil.sleep(2000);
        SkyXploreCharacterActions.submitForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "Ez a karakternév már foglalt.");

        SkyXploreCharacterActions.fillCharacterName(driver, userData2.getUsername());
        SleepUtil.sleep(2000);
        SkyXploreCharacterActions.submitForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Karakter elmentve.");

        SkyXploreMainMenuActions.editCharacter(driver);

        assertThat(SkyXploreCharacterActions.getBoxTitle(driver)).isEqualTo("Karakter szerkesztése");
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(userData2.getUsername());

        String newCharacterName = RegistrationParameters.validParameters()
            .getUsername();
        SkyXploreCharacterActions.fillCharacterName(driver, newCharacterName);
        SleepUtil.sleep(2000);
        SkyXploreCharacterActions.submitForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Karakter elmentve.");

        SkyXploreMainMenuActions.editCharacter(driver);
        assertThat(SkyXploreCharacterActions.getCharacterName(driver)).isEqualTo(newCharacterName);
    }
}
