package com.github.saphyra.apphub.integration.action.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.character.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.Future;

import static com.github.saphyra.apphub.integration.core.TestBase.EXECUTOR_SERVICE;

@Slf4j
public class SkyXploreUtils {
    public static Future<?> registerAndNavigateToMainMenu(BiWrapper<WebDriver, RegistrationParameters> biWrapper) {
        return registerAndNavigateToMainMenu(biWrapper.getEntity1(), biWrapper.getEntity2());
    }

    public static Future<?> registerAndNavigateToMainMenu(WebDriver driver, RegistrationParameters userData) {
        return EXECUTOR_SERVICE.submit(() -> {
            try {
                Navigation.toIndexPage(driver);
                IndexPageActions.registerUser(driver, userData);

                ModulesPageActions.openModule(driver, ModuleLocation.SKYXPLORE);
                SkyXploreCharacterActions.submitForm(driver);

                AwaitilityWrapper.createDefault()
                    .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
                    .assertTrue("MainMenu is not loaded.");
            } catch (Exception e) {
                log.error("Failed setting up user", e);
                throw e;
            }
        });
    }
}
